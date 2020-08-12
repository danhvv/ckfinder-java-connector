package example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	
	@RequestMapping("/welcome")
	public String showForm() {
		return "welcome";
	}

}