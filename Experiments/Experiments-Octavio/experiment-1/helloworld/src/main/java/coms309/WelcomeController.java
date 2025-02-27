package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309, you are currently reading the default message displayed when no string is specified after the url {'/string'}";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 3090, previously known as COM S 309: " + name;
    }

    @GetMapping("/{firstname}/{lastname}")
    public String welcomePath(@PathVariable String firstname, @PathVariable String lastname) {
        return "Localhost using port 8080 is sucessful! Your name is, " + firstname + " " + lastname;
    }
    // TO use this do /welcome?firstname={fName}&lastname={Lname}
    @GetMapping("/welcome")
    public String welcomeRequest(@RequestParam String firstname, @RequestParam String lastname) {
        return "Hello " + firstname + " " + lastname;
    }
}