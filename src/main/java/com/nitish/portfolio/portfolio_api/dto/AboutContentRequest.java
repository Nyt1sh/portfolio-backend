package com.nitish.portfolio.portfolio_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class AboutContentRequest {
    private String headline;
    private String subtitle;
    private String paragraph1;
    private String paragraph2;
    private List<String> chips;   // small highlight chips
    private String cvUrl;
}
