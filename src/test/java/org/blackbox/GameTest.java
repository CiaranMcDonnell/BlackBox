 package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GameTest {
    private HexagonManager mockHexManager;
    private Game testGame;
//    private GUI mockGUI;

    @BeforeEach
    public void setUp() {
        mockHexManager = Mockito.mock(HexagonManager.class);
//        mockGUI = Mockito.mock(GUI.class);
        testGame = new Game(mockHexManager);

//        Pane mockPane = Mockito.mock(Pane.class);
//        ObservableList<Node> mockList = FXCollections.observableArrayList();
//        Mockito.when(mockPane.getChildren()).thenReturn(mockList);
//        Mockito.when(mockGUI.getRoot()).thenReturn(mockPane);
    }

    @Test
    public void atomSelection_selectsSixAtoms_whenHexagonsAreAvailable() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.atomSelection();
        assertEquals(6, testGame.getAtomLocations().size());
    }

    @Test
    public void atomSelection_throwsException_whenNoHexagonsAreAvailable() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>());
        assertThrows(IllegalStateException.class, () -> testGame.atomSelection());
    }
//    This unit test is a WIP issues may need a refactor at a later date in my game class
//    @Test
//    public void atomReveal_revealsAtoms_whenAtomsAreSelected() {
//        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
//        testGame.atomSelection();
//        testGame.atomReveal();
//        verify(mockHexManager, times(6)).alterHexagon(0,0,0, RED);
//    }

    @Test
    public void atomReveal_doesNothing_whenNoAtomsAreSelected() {
        testGame.atomReveal();
        verify(mockHexManager, times(0)).alterHexagon(anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    void storeEntryPoints_storesEntryPointsForAllHexagons() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.storeEntryPoints();
        assertEquals(7, testGame.getEntryPointsMap().size());
    }

//    @Test
//    void getEntryPoints_returnsCorrectEntryPointsForGivenHex() {
//        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
//        testGame.storeEntryPoints();
//        List<Integer> entryPoints = testGame.getEntryPoints("0,0,0");
//        assertTrue(entryPoints.containsAll(Arrays.asList(0, 60, 120, 180, 240, 300)));
//    }

    @Test
    void getEntryPoints_returnsNullForNonexistentHex() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.storeEntryPoints();
        assertNull(testGame.getEntryPoints("2,2,-4"));
    }

//    @Test
//    void getEntryPointsMap_returnsCorrectMap() {
//        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
//        testGame.storeEntryPoints();
//        Map<String, List<Integer>> entryPointsMap = testGame.getEntryPointsMap();
//        assertEquals(7, entryPointsMap.size());
//        assertTrue(entryPointsMap.get("0,0,0").containsAll(Arrays.asList(0, 60, 120, 180, 240, 300)));
//    }
}