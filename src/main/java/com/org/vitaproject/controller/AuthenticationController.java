package com.org.vitaproject.controller;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.AuthenticationResponseDTO;
import com.org.vitaproject.model.dto.LoginRequestDTO;
import com.org.vitaproject.model.dto.UserRegisterDTO;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.model.entity.VerificationEntity;
import com.org.vitaproject.service.UserService;
import com.org.vitaproject.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) throws Exception {
        try {
            String response = authenticationService.userRegister(userRegisterDTO);
            return ResponseEntity.ok(response);
        } catch (MessageError ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationEntity verificationToken = authenticationService.getVerificationToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Invalid or Expired Token</title>\n" +
                    "    <style>\n" +
                    "        body, html {\n" +
                    "            height: 100%;\n" +
                    "            margin: 0;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            background-color: #ffe0e0;\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "        }\n" +
                    "        .alert {\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #b71c1c;\n" +
                    "            color: white;\n" +
                    "            text-align: center;\n" +
                    "            border-radius: 8px;\n" +
                    "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);\n" +
                    "            max-width: 300px;\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "        .alert strong {\n" +
                    "            display: block;\n" +
                    "            font-size: 1.2em;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        .alert button {\n" +
                    "            background-color: white;\n" +
                    "            color: #b71c1c;\n" +
                    "            border: none;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border-radius: 5px;\n" +
                    "            cursor: pointer;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .alert button:hover {\n" +
                    "            background-color: #7f0000;\n" +
                    "            color: white;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"alert\">\n" +
                    "        <strong>Invalid or Expired Token</strong>\n" +
                    "        Your token is either invalid or has expired. Please try again.\n" +
                    "        <button onclick=\"closeWindow()\">Okay</button>\n" +
                    "    </div>\n" +
                    "    <script>\n" +
                    "        function closeWindow() {\n" +
                    "            if (window.opener) {\n" +
                    "                window.close();\n" +
                    "            } else {\n" +
                    "                alert(\"You can close this window now.\");\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>\n";
        }
        UserEntity user = authenticationService.getUserEntity(verificationToken.getUsername());
        authenticationService.verifyUser(user);
        authenticationService.deleteToken(user.getUsername());
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email Verified</title>\n" +
                "    <style>\n" +
                "        body, html {\n" +
                "            height: 100%;\n" +
                "            margin: 0;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            background-color: #e0f7fa;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "        }\n" +
                "        .alert {\n" +
                "            padding: 20px;\n" +
                "            background-color: #00796b;\n" +
                "            color: white;\n" +
                "            text-align: center;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);\n" +
                "            max-width: 300px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "        .alert strong {\n" +
                "            display: block;\n" +
                "            font-size: 1.2em;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .alert button {\n" +
                "            background-color: white;\n" +
                "            color: #00796b;\n" +
                "            border: none;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            cursor: pointer;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "        .alert button:hover {\n" +
                "            background-color: #004d40;\n" +
                "            color: white;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"alert\">\n" +
                "        <strong>Email verified!</strong>\n" +
                "        Your email has been successfully verified.\n" +
                "        <button onclick=\"closeWindow()\">Okay</button>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        function closeWindow() {\n" +
                "            if (window.opener) {\n" +
                "                window.close();\n" +
                "            } else {\n" +
                "                alert(\"You can close this window now.\");\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    @PostMapping("/login")
    public AuthenticationResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authenticationService.login(loginRequestDTO);
    }
    @PostMapping("/send-verification-email")
    public String sendEmailVerification(@RequestParam String email) {
        return authenticationService.sendEmailVerification(email);
    }
}
