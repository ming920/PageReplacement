package PageReplacement;

/**
 * Frame 클래스는 페이지 교체 알고리즘에서 프레임을 표현한다.
 * 각 프레임은 하나의 페이지와 참조 여부를 저장한다.(Clock 알고리즘에 필요함)
 */

class Frame {
	char page; // 현재 프레임에 저장된 페이지
	boolean reference; // 참조 비트: Clock 알고리즘에서 사용됨
	
	/**
	 * 기본 생성자
	 * 페이지를 비워둔 상태로 초기화, 참조 비트는 false로 설정
	 */
	public Frame() {
		this.page = '\0';
		this.reference = false;
	}
	
	/**
	 * 특정 페이지를 저장하는 생성자
	 * 참조 비트는 false로 초기화(새로 들어온 페이지는 아직 참조되지 않았기 때문에 false로 설정)
	 * @param page 저장할 페이지 문자
	 */
	public Frame(char page) {
		this.page = page;
		this.reference = false;
	}
	
	// 프레임에 새로운 페이지를 설정하고, 참조 비트는 false로 초기화한다.
	public void set(char page) {
		this.page = page;
		this.reference = false;
	}
	
	// 프레임이 비어있는지 확인한다. 비어있다면 true를 반환한다.
	public boolean isEmpty() {
		return page == '\0';
	}
}
