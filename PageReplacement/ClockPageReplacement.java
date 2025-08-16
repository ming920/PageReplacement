package PageReplacement;

import java.util.*;

/**
 * Clock 페이지 교체 알고리즘을 구현한 클래스이다.
 * 참조 비트를 사용하여 페이지 교체 시 최근에 사용되지 않은 페이지를 우선 제거하는 방식이다.
 */

public class ClockPageReplacement implements PageReplacementPolicy {
	
	private int frameSize; // 사용할 프레임의 수
	private List<Character> referenceString; // 참조 문자열
	private List<Frame> frames; // 현재 메모리에 적재된 프레임들
	int pointer; // 시계 방향으로 가리키는 현재 포인터 위치
	private List<Boolean> hitHistory; // 매 시점별 Hit/Fault 여부 기록
	private int hitCount; // 총 Page Hit 횟수
	private int faultCount; // 총 Page Fault 횟수
	private List<List<Character>> frameSnapshots; // 각 시점의 프레임 상태 기록
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
		
		frames = new ArrayList<>();
		pointer = 0;
		hitHistory = new ArrayList<>();
		hitCount = 0;
		faultCount = 0;
		frameSnapshots = new ArrayList<>();
		
		for (char page : referenceString) {
			boolean hit = false;
			
			// 페이지가 이미 프레임 안에 있는지 확인
			for (Frame frame : frames) {
				if (frame.page == page) {
					frame.reference = true; // 참조되었으므로 reference flag를 true로 설정
					hit = true;
					break;
				}
			}
			
			if (hit) {
				hitHistory.add(true);
				hitCount++;
			} else {
				faultCount++;
				hitHistory.add(false);
				
				if (frames.size() < frameSize) {
					// 아직 프레임이 덜 찼으면 그냥 추가
					frames.add(new Frame(page));
					pointer = (pointer + 1) % frameSize;
				} else {
					// reference flag가 false인 페이지를 찾아 교체
					while (true) {
						Frame current = frames.get(pointer);
						if (current.reference) {
							current.reference = false; // 한 번의 기회를 더 주고 reference flag를 false로 변경
							pointer = (pointer + 1) % frameSize;
						} else {
							frames.set(pointer, new Frame(page)); // reference flag가 false면 해당 페이지 교체
							pointer = (pointer + 1) % frameSize;
							break;
						}
					}
				}
			}
			recordSnapshot();
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
		return "Clock";
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
