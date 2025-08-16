package PageReplacement;

import java.util.List;

/**
 * 다양한 페이지 교체 알고리즘들이 공통적으로 구현해야 할 메서드들을 정의한다.
 * FIFO, Optimal, LRU, Clock, LPR
 */

public interface PageReplacementPolicy {
	void setReferenceString(List<Character> referenceString); // 참조 문자열을 설정한다.
	void setFrameSize(int frameSize); // 프레임의 개수를 설정한다.
	void run(); // 페이지 교체 알고리즘을 실행한다.
	int getHitCount(); // Page Hit의 총 개수를 반환한다.
	int getFaultCount(); // Page Fault의 총 개수를 반환한다.
	List<Boolean> getHitHistory(); // 각 시점마다 페이지 참조가 Hit였는지 Fault였는지 기록을 반환한다.
	String getName(); // 알고리즘의 이름을 반환한다.
	List<List<Character>> getFrameSnapshots(); // 알고리즘 실행 과정에서 매 시점마다 프레임 상태를 리스트로 반환한다.
	long getExecutionTime(); // 알고리즘 실행에 걸린 시간
}
