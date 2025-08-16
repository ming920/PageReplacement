package PageReplacement;

import java.util.*;

/**
 * 다양한 페이지 교체 알고리즘을 실행하고 시뮬레이션 결과를 관리하는 역할을 담당한다.
 * 주어진 PageReplacementPolicy(FIFO, Optimal, LRU, Clock, LPR)를 실행한다.
 */

public class PageReplacementSimulator {
	private PageReplacementPolicy policy; // 사용할 페이지 교체 정책
	private List<Character> referenceString; // 참조 문자열
	private int frameSize; // 프레임의 수
	private List<List<Frame>> frameSnapshots = new ArrayList<>(); // 시점별 프레임 상태
	private long executionTime; // 알고리즘 실행에 걸린 시간
	
	// 생성자: 실행할 페이지 교체 알고리즘을 설정한다.
	public PageReplacementSimulator(PageReplacementPolicy policy) {
		this.policy = policy;
	}
	
	// 참조 문자열을 설정하고, 해당 정책에도 참조 문자열을 설정한다.
	public void setReferenceString(List<Character> referenceString) {
		this.referenceString = referenceString;
		policy.setReferenceString(referenceString);
	}
	
	// 프레임 수를 설정하고, 해당 정책에도 설정한다.
	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
		policy.setFrameSize(frameSize);
	}
	
	// 시뮬레이터 실행: 실제 정책 알고리즘의 run()의 메서드를 호출한다.
	public void runSimulator() {
		policy.run();
	}

	// 현재 설정된 페이지 교체 정책 객체를 반환한다.
	public PageReplacementPolicy getPolicy() {
		return policy;
	}

	// 참조 문자열을 반환한다.
	public String getReferenceString() {
		StringBuilder sb = new StringBuilder();
		for (Character ch : referenceString) {
			sb.append(ch);
		}
		
		return sb.toString();
	}

	// 현재 설정된 프레임의 수를 반환한다.
	public int getFrameSize() {
		return frameSize;
	}
}
