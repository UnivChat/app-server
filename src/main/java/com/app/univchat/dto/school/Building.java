package com.app.univchat.dto.school;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Building {
    K("김수환관"),
    N("니콜스관"),
    V("비루투스관"),
    D("다솔관"),
    B("학생미래인재관"),
    T("미카엘관"),
    L("베리타스관"),
    A("안드레아관"),
    BA("밤비노관");

    String buildingName;
}
