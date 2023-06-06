package in.rk.gflix.aggregator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.rk.gflix.aggregator.dto.RecommendedMovie;
import in.rk.gflix.aggregator.dto.UserGenre;
import in.rk.gflix.aggregator.service.AggregatorService;

@RestController
public class AggregatorController {
	
	@Autowired
	private AggregatorService aggregatorService;
	
	@GetMapping("/user/{loginId}")
	public List<RecommendedMovie> getRecommenedMovies(@PathVariable String loginId)
	{
		return aggregatorService.getUserMovieSuggestions(loginId);
	}

	@PutMapping("/user")
	public void setUserGenre(@RequestBody UserGenre userGenre)
	{
		aggregatorService.setUserGenre(userGenre);
	}
}
