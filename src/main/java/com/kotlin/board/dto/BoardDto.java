package com.kotlin.board.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BoardDto {
	//ronum은 안 가져와도 돼요. 왜냐면 화면에 안 찍어줄 거니까 
	// 근데 찍어주고 싶으면 가져와도 됩니다. 
	// 저는 bnum만 보여줄 거예요. 즉 삭제되면 삭제도니 번호 건너 듸고 보여줄거예요
	private int bnum;
	private String btitle;
	private String bcontents;
	private String bid;
	private String mname;
	private Timestamp bdate; //년월일 시분초의 정보가 들어감. 
	private int bviews;
}
