package gui.panel.display;

import config.GameConfiguration;
import engine.data.MapGame;
import engine.data.Fleet;
import engine.data.entity.Harbor;
import engine.data.entity.boats.Boat;
import engine.data.faction.Faction;
import engine.data.graph.GraphPoint;
import engine.data.trading.SeaRoad;
import engine.process.manager.FactionManager;
import gui.utilities.ImageStock;
import gui.process.PaintBackGround;
import gui.process.PaintEntity;
import gui.PopUp;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Kenan Ammad
 * Classe GameDisplay
 * @version 1.0
 */
public class GameDisplay extends JPanel {

    private final PaintEntity paintEntity;
    private final PaintBackGround paintBackGround;
    private Object currentObject;
    private boolean fastPathMode;

    /**
     * Typical constructor generating an GameDisplay
     */
    public GameDisplay(){
        this.paintEntity = new PaintEntity();
        this.paintBackGround = new PaintBackGround();
        currentObject = null;
        fastPathMode = false;
    }
    /**
     * It paints everything that has to be painted on graphics2D
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        double scale = Math.min((double)getWidth()/640,(double) getHeight() /360);
        g2d.scale(scale,scale);
        paintBackGround.paint(g2d,true);

        if(MapGame.getInstance().isTimeStop()){
            g2d.setColor(new Color(64, 64, 64, 100));
            g2d.fillRect(0,0, 640, 360);
            g2d.setColor(Color.black);
        }

        g2d.scale((double) 1 /GameConfiguration.GAME_SCALE, (double) 1 /GameConfiguration.GAME_SCALE);

        if(MapGame.getInstance().isGodMode()) {
            for (Faction faction : MapGame.getInstance().getLstFaction()) {
                for (Boat boat : faction.getLstBoat()) {
                    paintEntity.paint(boat, g2d);
                }
            }
        }

        for (Harbor harbor : MapGame.getInstance().getLstHarbor()){
            paintEntity.paint(harbor,g2d);
        }
        for(Boat boat : MapGame.getInstance().getPlayer().getVision()){
            paintEntity.paint(boat,g2d);
        }
        for(Boat boat : MapGame.getInstance().getPlayer().getLstBoat()){
            paintEntity.paintPlayer(boat,g2d);
        }
        for (Harbor harbor : MapGame.getInstance().getPlayer().getLstHarbor()){
            paintEntity.paintPlayer(harbor,g2d);
        }
        for(SeaRoad seaRoad: MapGame.getInstance().getPlayer().getLstSeaRouts()){
            paintEntity.paint(seaRoad,g2d);
        }
        if(fastPathMode){
            g2d.setColor(new Color(4, 4, 62,75));
            for(GraphPoint graphPoint : MapGame.getInstance().getMapGraphPoint()){
                g2d.fillOval(graphPoint.getX()-15, graphPoint.getY()-15,30,30);
            }
            g2d.setColor(Color.BLACK);
        }
        if(currentObject!= null){
            if(currentObject instanceof Boat){
                Boat currentBoat = (Boat) currentObject;
                ArrayList<GraphPoint> path = new ArrayList<>(currentBoat.getPath());
                path.subList(0,currentBoat.getIPath()).clear();
                path.add(0,new GraphPoint(currentBoat.getPosition(),"temp-GameDisplay"));
                paintEntity.paintPath(path,ImageStock.colorChoice(currentBoat.getColor()),g2d);
                paintEntity.paintHITBOX(currentBoat.getPosition(), new Color(125, 130, 200,200), (int) (GameConfiguration.HITBOX_BOAT+5),g2d);
                paintEntity.paintSprite(currentBoat.getPosition(),ImageStock.getImage(currentBoat),g2d, currentBoat.getAngle());
            }
            else if(currentObject instanceof Fleet){
                Fleet currentFleet = (Fleet) currentObject;
                for (Boat boat : currentFleet.getArrayListBoat()){
                    paintEntity.paintHITBOX(boat.getPosition(), new Color(125, 130, 200,150),(int) (GameConfiguration.HITBOX_BOAT+5),g2d);
                }
                for (Boat boat : currentFleet.getArrayListBoat()){
                    paintEntity.paintSprite(boat.getPosition(),ImageStock.getImage(boat),g2d, boat.getAngle());
                }
                paintEntity.paintPath(currentFleet.getPath(),ImageStock.colorChoice(FactionManager.getInstance().getMyFaction(currentFleet).getColor()),g2d);
            }
            else if(currentObject instanceof SeaRoad){
                SeaRoad currentSeaRoad = (SeaRoad) currentObject;
                paintEntity.paintPath(currentSeaRoad.getPath(),ImageStock.colorChoice(currentSeaRoad.getSellerHarbor().getColor()),g2d);

            }
        }
        paintEntity.paintChaseMap(g2d);

        for (Harbor harbor : MapGame.getInstance().getLstHarbor()){
            paintEntity.paintHP(harbor,g2d);
        }

        ArrayList<PopUp> lstPopUp = new ArrayList<>();
        lstPopUp.addAll(MapGame.getInstance().getLstPopUp());
        for (PopUp popUp : lstPopUp){
            paintEntity.paint(popUp,g2d);
        }
        g2d.scale(GameConfiguration.GAME_SCALE,GameConfiguration.GAME_SCALE);
    }

    public PaintBackGround getPaintBackGround() { return paintBackGround; }


    public void setCurrentObject(Object currentObject) {
        this.currentObject = currentObject;
    }

    public boolean isFastPathMode() {
        return fastPathMode;
    }

    public void setFastPathMode(boolean fastPathMode) {
        this.fastPathMode = fastPathMode;
    }
}
