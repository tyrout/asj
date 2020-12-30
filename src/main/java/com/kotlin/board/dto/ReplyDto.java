package com.kotlin.board.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ReplyDto {
   private int r_num; // 시퀀스로 자동생성 
   private int r_bnum;
   private String r_contents;
   @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
   private Timestamp r_date;
   private String r_id;
}
//data 처리시 json 객체로 변환할때
 //시간값에 대한 출력형식을 pattern 으로 지정 
