package PageReplacement;

import java.util.*;

import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * JavaFX 기반의 시각화 인터페이스를 제공하여
 * 다양한 페이지 교체 알고리즘(FIFO, Optimal, LRU, Clock, LPR)을 실행하고
 * 그 결과를 출력 및 시각적으로 표현한다.
 */

public class SimulatorUI {
	
	// UI 컴포넌트 선언
	private ComboBox<String> policyBox = new ComboBox<>(); // 알고리즘 선택 콤보박스
	private TextField referenceStringInput = new TextField(); // 참조 문자열 입력 필드
	private TextField frameSizeInput = new TextField(); // 프레임 수 입력 필드
	private TextArea outputArea = new TextArea(); // 결과 출력 영역
	private PieChart pieChart = new PieChart(); // Hit/Fault 비율 파이차트
	private GridPane frameGrid = new GridPane(); // 시각적 프레임 스냅샷 그리드
	
	// UI 레이아웃을 구성하여 BorderPane 형태로 반환한다.
	public BorderPane createContent() {
		// 콤보박스 초기화
		policyBox.getItems().addAll("FIFO", "Optimal", "LRU", "Clock", "LPR");
		policyBox.setValue("FIFO");
		
		// 실행 버튼
		Button runButton = new Button("Run");
		runButton.setOnAction(e -> runSimulation());
		
		// 랜덤 참조 문자열 생성 버튼
		Button randomButton = new Button("Random");
		randomButton.setOnAction(e -> {
			referenceStringInput.setText(ReferenceStringGenerator.generateRandom());
		});
		
		// 기본 프레임 수
		frameSizeInput.setText("4");
		HBox controls = new HBox(10, 
				new Label("Policy : "), policyBox, 
				new Label("Reference String : "), referenceStringInput, randomButton, 
				new Label("Frames : "), frameSizeInput, runButton);
		
		controls.setAlignment(Pos.CENTER);
		controls.setPadding(new Insets(10));
		
		// 프레임 그리드 영역 (가로 스크롤 가능)
		ScrollPane gridScroll = new ScrollPane(frameGrid);
		gridScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		gridScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		gridScroll.setFitToHeight(true);
		gridScroll.setPannable(true);
		
		// 결과 출력 영역 설정
		outputArea.setEditable(false);
		outputArea.setFont(Font.font("Monospaced", 12));
		outputArea.setWrapText(true);	
		pieChart.setPrefSize(300, 300);
		
		// 하단 결과 영역 구성
		HBox bottom = new HBox(10,
				new VBox(new Label("Result : "), outputArea),
				new VBox(new Label("Hit/Fault Ratio : "), pieChart)
				);
		
		bottom.setAlignment(Pos.TOP_LEFT);
		bottom.setPadding(new Insets(10));
		HBox.setHgrow(outputArea, Priority.ALWAYS);
		VBox.setVgrow(outputArea, Priority.ALWAYS);
		
		// 전체 레이아웃 구성
		BorderPane root = new BorderPane();
		root.setTop(controls);
		root.setCenter(gridScroll);
		root.setBottom(bottom);
		return root;
	}
	
	// 사용자가 선택한 알고리즘과 입력값에 따라 시뮬레이션을 실행한다.
	private void runSimulation() {
		String algorithm = policyBox.getValue();
		List<Character> referenceString = new ArrayList<>();
		for (char ch : referenceStringInput.getText().toCharArray()) {
			referenceString.add(ch);
		}
		int frameSize = Integer.parseInt(frameSizeInput.getText());
		
		// 알고리즘 선택
		PageReplacementPolicy policy;
		switch (algorithm) {
			case "Optimal":
				policy = new OptimalPageReplacement();
				break;
			case "LRU":
				policy = new LRUPageReplacement();
				break;
			case "Clock":
				policy = new ClockPageReplacement();
				break;
			case "LPR":
				policy = new LPRPageReplacement();
				break;
			default:
				policy = new FIFOPageReplacement();
		}
		
		// 시뮬레이터 실행
		PageReplacementSimulator simulator = new PageReplacementSimulator(policy);
		simulator.setReferenceString(referenceString);
		simulator.setFrameSize(frameSize);
		simulator.runSimulator();
		
		updateOutput(simulator); // 텍스트 출력 업데이트
		updateGrid(simulator); // 시각적 그리드 업데이트
	}
	
	// 텍스트 출력 영역과 파이차트를 업데이트한다.
	private void updateOutput(PageReplacementSimulator simulator) {
		PageReplacementPolicy policy = simulator.getPolicy();
		
		StringBuilder sb = new StringBuilder();
		sb.append("===").append(policy.getName()).append(" Result ===\n");
		sb.append("Reference String : ").append(simulator.getReferenceString()).append("\n");
		sb.append("Run Time : ").append(simulator.getPolicy().getExecutionTime()).append(" µs").append("\n");
		sb.append("Hit : ").append(policy.getHitCount()).append(", Fault : ").append(policy.getFaultCount()).append("\n");
		
		String referenceString = simulator.getReferenceString();
		List<Boolean> hitHistory = policy.getHitHistory();
		List<List<Character>> frameSnapshots = policy.getFrameSnapshots();
		int frameSize = simulator.getFrameSize();
		boolean firstFull = false;
		
		for (int i = 0; i < referenceString.length(); ++i) {
			char ch = referenceString.charAt(i);
			boolean isHit = hitHistory.get(i);
			List<Character> curr = frameSnapshots.get(i);
			if (i > 0) firstFull = frameSnapshots.get(i - 1).size() == frameSize;
			
			String status;
			if (isHit) status = "Hit";
			else {
				if (firstFull) status = "Migrated";
				else status = "Fault";
			}
			
			sb.append(ch).append(" : ").append(status).append("\n");
		}
		
		outputArea.setText(sb.toString());
		
		// 파이차트 업데이트
		pieChart.getData().clear();
		PieChart.Data hitData = new PieChart.Data("Hit: " + policy.getHitCount(), policy.getHitCount());
		PieChart.Data faultData = new PieChart.Data("Fault: " + policy.getFaultCount(), policy.getFaultCount());
		pieChart.getData().addAll(hitData, faultData);
		pieChart.setLabelsVisible(true);
	}
	
	// 시각적으로 프레임 상태를 시간 순서대로 그리드에 표시한다.
	private void updateGrid(PageReplacementSimulator simulator) {
		frameGrid.getChildren().clear();
		frameGrid.setGridLinesVisible(false);
		frameGrid.setHgap(4);
		frameGrid.setVgap(4);
		frameGrid.setPadding(new Insets(10));
		
		PageReplacementPolicy policy = simulator.getPolicy();
		List<Boolean> hitHistory = policy.getHitHistory();
		List<List<Character>> frameSnapshots = policy.getFrameSnapshots();
		int frameSize = simulator.getFrameSize();
		String referenceString = simulator.getReferenceString();
		
		// 상단 (참조 문자열)
		for (int i = 0; i < referenceString.length(); ++i) {
			Label head = new Label(String.valueOf(referenceString.charAt(i)));
			head.setMinSize(40, 40);
			head.setAlignment(Pos.CENTER);
			head.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-border-width: 0");
			frameGrid.add(head, i, 0);
		}
		
		// 프레임 내용
		for (int time = 0; time < frameSnapshots.size(); ++time) {
			List<Character> curr = frameSnapshots.get(time);
			char referenceChar = referenceString.charAt(time);
			boolean isHit = hitHistory.get(time);
			boolean firstFull = false;
			if (time > 0) firstFull = frameSnapshots.get(time - 1).size() == frameSize;
			
			for (int row = 0; row < frameSize; ++row) {
				Label label;
				
				if (row < curr.size()) {
					char page = curr.get(row);
					label = new Label(String.valueOf(page));
					label.setStyle("-fx-border-color: gray; " + "-fx-border-width: 1px;");
					label.setMinSize(40, 40);
					label.setAlignment(Pos.CENTER);
					
					if (page == referenceChar) {
						if (isHit) {
							label.setStyle(label.getStyle() + "-fx-background-color: green;");
						} else {
							if (firstFull) {
								label.setStyle(label.getStyle() + "-fx-background-color: purple;");
							} else {
								label.setStyle(label.getStyle() + "-fx-background-color: red");
							}
						}
					} 
				} else {
					label = new Label("");
					label.setStyle("-fx-border-color: gray;");
					label.setMinSize(40, 40);
					label.setAlignment(Pos.CENTER);
				}
 				frameGrid.add(label, time, row + 1);
			}
		}
	}
}
