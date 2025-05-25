import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.agrigenius360.R
import com.example.agrigenius360.AppDatabase
import com.example.agrigenius360.UsersDAO
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpVerificationScreen(usersDAO: UsersDAO, navController: NavController, phoneNumber: String, otp: String ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
//    val db = remember {
//        Room.databaseBuilder(
//            context,
//            AppDatabase ::class.java,
//            "users"
//        ).build()
//    }

    val focusRequesters = List(6) { remember { FocusRequester() } }
    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        otp.forEachIndexed { index, c ->
            otpDigits[index] = c.toString()
        }
    }

    fun verifyOtp() {
        scope.launch {
            System.out.println("######phoneNumber"+phoneNumber)
            val user = usersDAO.findByPhone(phoneNumber)
            System.out.println("######33"+user)
            if(user != null){
                val inputOtp = otpDigits.joinToString("")
                val currentTime = System.currentTimeMillis()
                if(user.lastOtp == inputOtp){
                    if(user.lastOtpExpiry != null && user.lastOtpExpiry > currentTime){
                        usersDAO.removeOtp(phoneNumber)
                        navController.navigate("home")
                    }else{
                        errorMessage = "OTP has expired"
                    }
                }else{
                    errorMessage = "Invalid OTP"
                }
            }else{
                errorMessage = "User does not exist"
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.applogo2), contentDescription = "App Logo", modifier =  Modifier.size(200.dp))
            Text(text = "AgriGenius360", fontSize = 32.sp, fontWeight = FontWeight.Bold )
            Text("We've sent a verification code for ${phoneNumber}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text("[Phone number here]", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpDigits.forEachIndexed { index, digit ->
                    OutlinedTextField(
                        value = digit,
                        onValueChange = { newDigit ->
                            if (newDigit.length <= 1 && newDigit.all { it.isDigit() }) {
                                otpDigits[index] = newDigit
                                if (newDigit.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                        ),
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .focusRequester(focusRequesters[index])
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small),
                        singleLine = true
                    )
                }
            }

            if(errorMessage.isNotEmpty()){
                Spacer(modifier = Modifier.height((15.dp)))
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    verifyOtp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text("Verify")
            }
        }
    }
}
