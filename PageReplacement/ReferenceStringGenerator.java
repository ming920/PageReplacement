package PageReplacement;

import java.util.*;

/**
 * 페이지 교체 알고리즘 테스트용 랜덤 참조 문자열을 생성하는 클래스이다.
 */

public class ReferenceStringGenerator {
	
	/**
	 * A ~ Z 사이의 대문자 알파벳으로 이루어진 랜덤 참조 문자열을 생성한다.
	 * 길이는 6 ~ 24 사이에서 랜덤하게 설정된다.
	 * @return 랜덤 참조 문자열
	 */
	
	public static String generateRandom() {
		StringBuilder sb = new StringBuilder(); // 문자열을 효율적으로 생성하기 위한 StringBuilder 사용
		Random random = new Random();
		
		int length = random.nextInt(6, 25);
		for (int i = 0; i < length; ++i) {
			char ch = (char) ('A' + random.nextInt(26));
			sb.append(ch);
		}
		
		return sb.toString(); // 완성된 참조 문자열 반환
	}
}
