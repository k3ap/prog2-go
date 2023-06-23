package gui;

import logika.GoGameType;
import vodja.GameType;

/**
 * The parameters needed to start a new Go game.
 */
public record GameParams(GoGameType goGameType, GameType gameType, IntelligencePair ip, int size) {

}
