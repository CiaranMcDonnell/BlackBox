package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GameTest {
    private HexagonManager mockHexManager;
    private Game testGame;
    private GUI mockGUI;

    @BeforeEach
    public void setUp() {
        mockHexManager = Mockito.mock(HexagonManager.class);
        mockGUI = Mockito.mock(GUI.class);
        testGame = new Game(mockHexManager, mockGUI);

        Pane mockPane = Mockito.mock(Pane.class);
        ObservableList<Node> mockList = FXCollections.observableArrayList();
        Mockito.when(mockPane.getChildren()).thenReturn(mockList);
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

    @Test
    public void atomReveal_doesNothing_whenNoAtomsAreSelected() {
        testGame.atomReveal();
        verify(mockHexManager, times(0)).alterHexagon(anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    void storeEntryPoints_storesEntryPointsForAllHexagons() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("4,0,0", "1,-4,0", "1,0,-1", "0,4,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.storeEntryPoints();
        assertEquals(3, testGame.getEntryPointsMap().size());
    }

    @Test
    void getEntryPoints_returnsNullForNonexistentHex() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.storeEntryPoints();
        assertNull(testGame.getEntryPoints("2,2,-4"));
    }
    @Test
    void atomReveal_altersHexagons_whenAtomsAreSelected() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.atomSelection();
        testGame.atomReveal();
        verify(mockHexManager, times(6)).alterHexagon(anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    void atomsEffectiveRange_returnsCorrectNeighbors_whenAtomsAreSelected() {
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        when(mockHexManager.getNeighborLocations(anyInt(), anyInt(), anyInt())).thenReturn(Arrays.asList("1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1", "1,-1,0"));
        testGame.atomSelection();
        Map<String, List<String>> atomNeighbors = testGame.atomsEffectiveRange();
        assertEquals(6, atomNeighbors.size());
        assertTrue(atomNeighbors.values().stream().allMatch(neighbors -> neighbors.size() == 6));
    }
    @Test
    void scoreTracker_updatesScore_whenGameEnds() {
        when(mockGUI.getOrangeHexButtons()).thenReturn(new HashMap<>(Map.of("0,0,0", 0, "1,-1,0", 60, "1,0,-1", 120, "0,1,-1", 180, "-1,1,0", 240, "-1,0,1", 300)));
        when(mockHexManager.getAllHexagonLocations()).thenReturn(new HashSet<>(Arrays.asList("0,0,0", "1,-1,0", "1,0,-1", "0,1,-1", "-1,1,0", "-1,0,1", "0,-1,1")));
        testGame.atomSelection();
        testGame.raysShot = 10;
        testGame.scoreTracker();
        assertEquals(15, testGame.getScore());
    }

}