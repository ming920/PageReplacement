package PageReplacement;

import java.util.*;

/**
 * 최적의 페이지 교체 알고리즘을 구현한다.
 * 앞으로 가장 늦게 사용될 페이지 또는 더 이상 사용되지 않을 페이지를 교체 대상으로 선택한다.
 * 이론적으로 가장 적은 수의 페이지 폴트가 발생한다.
 */

public class OptimalPageReplacement implements PageReplacementPolicy {
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
		
		frames = new ArrayList<>();
		hitHistory = new ArrayList<>();
		hitCount = 0;
		faultCount = 0;
		frameSnapshots = new ArrayList<>();
		
		for (int i = 0; i < referenceString.size(); ++i) {
			char page = referenceString.get(i);
			boolean hit = false;
			
			// 현재 프레임에 페이지가 이미 있는지 확인
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
				
				if (frames.size() == frameSize) {
					int farthestIndex = -1; // 가장 나중에 사용될 시점
					int farthestFrameIndex = 0; // 교체할 프레임의 index
					
					// 각 프레임에 대해 앞으로 이 페이지가 언제 다시 사용되는지 탐색
					for (int j = 0; j < frames.size(); ++j) {
						int nextUse = Integer.MAX_VALUE; // 사용되지 않으면 매우 먼 미래로 간주
						for (int k = i + 1; k < referenceString.size(); ++k) {
							if (referenceString.get(k) == frames.get(j).page) {
								nextUse = k;
								break;
							}
						}
						
						// 가장 나중에 사용될 페이지를 교체 대상으로 설정
						if (nextUse > farthestIndex) {
							farthestIndex = nextUse;
							farthestFrameIndex = j;
						}
					}
					
					// 해당 프레임 위치에 새로운 페이지로 교체
					frames.set(farthestFrameIndex, new Frame(page));
				} else {
					// 아직 프레임이 덜 찼으면 그냥 추가
					frames.add(new Frame(page)); 
				}
			}
			recordSnapshot();
		}
		
		long end = System.nanoTime(); // 종료 시간 측정
		executionTime = (end - start) / 1_000;
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
		return "Optimal";
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
