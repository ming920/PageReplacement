package PageReplacement;

import java.util.*;

/**
 * LRU(Least Recently Used) 페이지 교체 알고리즘을 구현한 클래스이다.
 * 가장 오래 전에 사용된 페이지를 교체대상으로 선택한다.
 */

public class LRUPageReplacement implements PageReplacementPolicy {
	private List<Character> referenceString; // 페이지 참조 문자열
	private int frameSize; // 프레임의 개수
	private List<Frame> frames; // 현재 메모리에 적재된 프레임들
	private List<Boolean> hitHistory; // 각 참조 시점별 Hit/Fault 여부 기록
	private int hitCount; // 총 Hit 횟수
	private int faultCount; // 총 Fault 횟수
	private List<List<Character>> frameSnapshots; // 각 시점별 프레임 상태 기록
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
		
		frames = new LinkedList<>(); // Queue를 이용해 구현
		hitHistory = new ArrayList<>();
		hitCount = 0;
		faultCount = 0;
		frameSnapshots = new ArrayList<>();
		
		for (char page : referenceString) {
			boolean hit = false;
			
			// 프레임 안에 해당 페이지가 있는지 검사
			Iterator<Frame> it = frames.iterator();
			while (it.hasNext()) {
				Frame frame = it.next();
				if (frame.page == page) {
					hit = true;
					it.remove(); // 기존 위치에서 제거
					break;
				}
			}
			
			if (hit) {
				hitCount++;
				hitHistory.add(true);
				frames.addLast(new Frame(page)); // 가장 최근 사용된 페이지로 갱신
			} else {
				faultCount++;
				hitHistory.add(false);
				
				// 프레임이 가득 찼으면 가장 오래된 페이지 제거
				if(frames.size() == frameSize) frames.removeFirst(); 
				frames.addLast(new Frame(page)); // 새 페이지 추가 (가장 최근 사용한 페이지로 갱신)
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
		return "LRU";
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
