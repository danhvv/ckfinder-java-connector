package example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	
	@RequestMapping("/welcome")
	@CrossOrigin(origins = "http://mkmc.vn")
	public String showForm() {
		return "welcome";
	}

}