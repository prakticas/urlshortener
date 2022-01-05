package es.unizar.urlshortener.infrastructure.delivery

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import java.util.*

@Controller
class InitIndex {
    @GetMapping
    fun index(model: Model): String {
        model["uuid"] = UUID.randomUUID().toString()
        return "index"
    }

}