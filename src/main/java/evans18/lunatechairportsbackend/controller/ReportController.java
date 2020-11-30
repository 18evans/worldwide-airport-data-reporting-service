package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.manager.ReportManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportManager reportManager;

    @GetMapping("/top10")
    List<Pair<String, Long>> reportTop10AirportCountCountries(@RequestParam(value = "is_most", defaultValue = "true") boolean isMost) {
        try {
            return reportManager.reportTop10CountriesWithMostOrLeastAirports(isMost);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal database server temporarily unavailable. :(");
        }
    }

}
