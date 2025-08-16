package PageReplacement;

import java.util.*;

/**
 * LPR(Lowest Probability Replacement) 알고리즘을 구현한다.
 * 이전 페이지 참조 이력을 기반으로 다음 페이지 등장 확률을 계산하고,
 * 해당 확률이 가장 낮은 페이지를 교체 대상으로 선택한다.
 */

public class LPRPageReplacement implements PageReplacementPolicy {
	private List<Character> referenceString; // 페이지 참조 문자열
	private int frameSize; // 프레임의 수
	private List<Frame> frames; // 현재 메모리에 적재된 프레임들
	
	// 각 페이지가 등장한 이후에 어떤 페이지가 몇 번 등장했는지 기록
	private Map<Character, Map<Character, Integer>> nextPageCount;
	
	private List<Boolean> hitHistory; // 각 참조 시점의 Hit 여부
	private int hitCount; // 총 Hit 횟수
	private int faultCount; // 총 Fault 횟수
	private List<List<Character>> frameSnapshots; // 프레임 상태 스냅샷
	private long executionTime = 0; // 실행 시간 저장 변수
	
	// 현재 시점의 프레임 상태를 기록한다.
	private void recordSnapshot() {
		List<Character> snapshot = new ArrayList<>();
		for (Frame frame : frames) {
			snapshot.add(frame.page);
		}
		frameSnapshots.add(snapshot);
	}
	
	/**
	 * 현재 참조 중인 페이지(curr)를 기준으로,
	 * 다음 등장할 확률이 가장 낮은 페이지를 교체 대상으로 선택한다.
	 * 
	 * 이 메서드는 과거 참조 이력(nextPageCount)을 기반으로 
	 * "지금 들어온 페이지(curr) 다음에는 어떤 페이지들이 얼마나 자주 등장했는가?"를 분석하여
	 * 현재 프레임에 존재하는 페이지들 중 앞으로 등장 확률이 가장 낮은 페이지를 찾아 교체한다.
	 * 
	 * @param curr 현재 참조 중인 페이지
	 * @return 교체 대상이 될 프레임의 index
	 */
	private int findVictim(Character curr) {
		// curr 페이지가 과거에 어떤 페이지들이 다음에 나왔는지에 대한 카운트 맵을 가져온다.
		// 예: nextPageCount.get('A') = {'B' : 5, 'C' : 3, 'D' : 1 }
		Map<Character, Integer> pageCount = nextPageCount.get(curr);
		
		// 과거 정보가 없거나 curr 이후의 등장 기록이 없다면
		// 확률을 비교할 기준이 없으므로 기본적으로 0번 프레임을 victim으로 선택한다.
		if (pageCount == null || pageCount.isEmpty()) return 0; 
		
		int total = 0; // 총 등장 횟수
		for (Integer count : pageCount.values()) {
			total += count;
		}
		
		// total이 0이라는 건 curr 이후 어떤 페이지도 등장하지 않았다는 뜻
		// 데이터 부족으로 0번 프레임을 victim으로 선택한다.
		if (total == 0) return 0; 
		
		double minProb = Double.MAX_VALUE; // 현재까지 찾은 최소 확률
		int victimIndex = 0; // 교체 대상이 될 프레임 index
		
		// 현재 프레임에 들어 있는 각 페이지에 대해 등장 확률 계산
		for (int i = 0; i < frames.size(); ++i) {
			char victimPage = frames.get(i).page; // 현재 프레임에 있는 페이지
			int count = pageCount.getOrDefault(victimPage, 0); // curr 이후에 이 페이지가 등장한 횟수(없으면 0)
			double prob = (double)count / total; // (현재 프레임 다음에 나오는 페이지의 횟수) / (총 횟수) 
			
			// 확률이 가장 낮은 프레임 index 설정
			if (prob < minProb) {
				minProb = prob;
				victimIndex = i;
			}
		}
		
		// 최종적으로 확률이 가장 낮았던 페이지의 위치 반환
		return victimIndex;
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
		nextPageCount = new HashMap<>();
		hitHistory = new ArrayList<>();
		hitCount = 0;
		faultCount = 0;
		frameSnapshots = new ArrayList<>();
		
		char prev = '\0'; // 직전 페이지
		
		for (int i = 0; i < referenceString.size(); ++i) {
			char curr = referenceString.get(i);
			boolean hit = false;
			
			// 이전 페이지(prev)  ->  현재 페이지(curr) 패턴 기록
			if (i > 0) {
				nextPageCount.putIfAbsent(prev, new HashMap<>());
				Map<Character, Integer> pageCount = nextPageCount.get(prev);
				pageCount.put(curr, pageCount.getOrDefault(curr, 0) + 1);
			}
			
			// 현재 페이지가 이미 프레임에 있는지 검사
			for (Frame frame : frames) {
				if (frame.page == curr) {
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
					// 교체 대상 선택 및 교체
					int victimIndex = findVictim(curr);
					frames.set(victimIndex, new Frame(curr));
				} else {
					frames.add(new Frame(curr));
				}
			}
			
			recordSnapshot();
			prev = curr; // 다음 패턴 분석을 위한 현재 페이지를 저장
		}
		
		long end = System.nanoTime();
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
		return "LPR";
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
