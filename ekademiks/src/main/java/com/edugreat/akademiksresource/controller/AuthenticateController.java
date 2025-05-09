package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.amqp.notification.consumer.NotificationConsumer;
import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.chat.amq.consumer.ChatConsumer;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticateController {
	private final AppAuthInterface appInterface;

	@Autowired
	private ChatConsumer chatConsumer;

	@Autowired
	private NotificationConsumer notificationConsumer;

	@PostMapping("/sign-up")
	@JsonView(UserView.class)
	public int signUp(@RequestBody @Valid AppUserDTO userDTO) throws Exception {

		return appInterface.signUp(userDTO);

	}

	@PostMapping("/sign-in")
	@JsonView(UserView.SigninView.class)
	public ResponseEntity<AppUserDTO> signIn(@RequestBody @Valid AuthenticationRequest request,
			@RequestParam String role) {

		return ResponseEntity.ok(appInterface.signIn(request, role));
	}

	// Controller endpoint for requesting new access token upon token expiration
	@PostMapping("/refresh-token")
	@JsonView(UserView.SigninView.class)
	public ResponseEntity<AppUserDTO> refreshToken(@RequestBody Map<String, String> request,
			HttpServletResponse respone) throws IOException {

		;

		final String token = request.get("refreshToken");
		;

		return ResponseEntity.ok(appInterface.generateNewToken(token, respone));

	}

	@PostMapping("/disconnect")
	public ResponseEntity<Object> disconnectFromSSE(@RequestBody Map<Integer, Integer> mapObj) {

		try {

			chatConsumer.disconnectGroup(mapObj);

			Integer studentId = mapObj.get(mapObj.keySet().toArray()[0]);

			notificationConsumer.disconnectFromSSE(studentId);
		}

		catch (Exception e) {

			

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		}

		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	@GetMapping("/cached/user")
	public ResponseEntity<Object> cachedUser(@RequestParam ("cache")String key){
		
	
		
		try {
			
			return  ResponseEntity.ok(appInterface.getCachedUser(key));
		} catch (Exception e) {
			
			System.out.println(e.getLocalizedMessage());
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
