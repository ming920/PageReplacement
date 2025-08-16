package PageReplacement;

import java.util.*;

/**
 * FIFO 페이지 교체 알고리즘을 구현한 클래스이다.
 * 가장 먼저 들어온 페이지를 제거하는 방식으로 페이지 교체를 수행한다.
 */

public class FIFOPageReplacement implements PageReplacementPolicy {
	private List<Character> referenceString; // 참조 문자열
	private int frameSize; // 프레임 개수
	private Queue<Frame> frames; // 현재 메모리에 적재된 프레임들
	private List<Boolean> hitHistory; // 메모리 참조 시 Hit/Fault 여부를 저장
	private int hitCount; // 총 Page hit 횟수
	private int faultCount; // 총 Page Fault 횟수
	private List<List<Character>> frameSnapshots; // 매 시점마다 프레임 상태를 저장한 리스트
	private long executionTime = 0; // 실행 시간 저장 변수
	
	// 현재 시점의 프레임 상태를 복사하여 frameSnapshots에 저장한다.
	private void recordSnapshot() {
		List<Character> snapshot = new ArrayList<>();
		for (Frame frame : frames) {
			snapshot.add(frame.page);
		}
		frameSnapshots.add(snapshot);
	}
	
	@Override
	public void setReferenceString(List<Character> referenceString) {
		this.referenceString = referenceString;
	}

	@Override
	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}
	
	@Override
	public void run() {
		long start = System.nanoTime(); // 시작 시간 측정
		
		frames = new LinkedList<>(); // Queue 사용
		hitHistory = new ArrayList<>();
		hitCount = 0;
		faultCount = 0;
		frameSnapshots = new ArrayList<>();
		
		for (char page : referenceString) {
			boolean hit = false;
			
			// 현재 프레임에 페이지가 존재하는지 확인
			for (Frame frame : frames) {
				if (frame.page == page) {
					hit = true;
					break;
				}
			}
			
			if (hit) {
				hitCount++;
				hitHistory.add(true);
			} else {
				faultCount++;
				hitHistory.add(false);
				if (frames.size() == frameSize) frames.poll(); // 프레임 가득 찼으면 가장 먼저 들어온 프레임 제거
				frames.offer(new Frame(page)); // 새로운 페이지 삽입
			}
			recordSnapshot(); // 현재 시점의 프레임 상태 저장
		}
		long end = System.nanoTime(); // 종료 시간 측정
		executionTime = (end - start) / 1_000; // 밀리초로 변환
	}
	
	@Override
	public int getHitCount() {
		return hitCount;
	}
	
	@Override
	public int getFaultCount() {
		return faultCount;
	}

	@Override
	public List<Boolean> getHitHistory() {
		return hitHistory;
	}

	@Override
	public String getName() {
		return "FIFO";
	}

	@Override
	public List<List<Character>> getFrameSnapshots() {
		return frameSnapshots;
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}
}
