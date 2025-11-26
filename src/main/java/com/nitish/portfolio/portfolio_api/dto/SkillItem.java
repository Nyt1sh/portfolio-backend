package com.nitish.portfolio.portfolio_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class SkillItem {
    private String title;
    private String desc;
    private List<String> list;
}
