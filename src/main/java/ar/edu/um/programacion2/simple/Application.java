package ar.edu.um.programacion2.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import ar.edu.um.programacion2.simple.model.User;
import ar.edu.um.programacion2.simple.service.UserService;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private UserService userService;

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setUserName("ServicioFranquicia");
		String token = Jwts.builder()
                    .setSubject(user.getUserName())
                    .claim("userId", user.getId())
                    .signWith(SignatureAlgorithm.HS256, "secreto")
                    .compact();
		user.setToken(token);
		userService.save(user);
	}
}
