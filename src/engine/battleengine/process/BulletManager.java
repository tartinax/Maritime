package engine.battleengine.process;

import engine.battleengine.data.Battle;
import engine.battleengine.data.Bullet;
import config.GameConfiguration;
import engine.data.entity.boats.Boat;
import engine.data.Fleet;
import engine.process.manager.BoatManager;
import engine.data.MapGame;
import gui.PopUp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manager de classe utile pour gérer la partie déplacement et collisions des {@link Bullet}.
 * @author Gauthier Defrance
 * @author Kenan Ammad
 * @version 1.0
 */
public class BulletManager {

    private final Battle battle;

    /**
     * Constructor of the BulletManager.
     * @param battle {@link Battle} the data class
     */
    public BulletManager(Battle battle) {
        this.battle = battle;
    }

    /**
     * Method that "tick" the BulletManager,
     * moving the Bullets and remove the dead bullets.
     */
    public void tick(){
        moveAllBullets();
        collideAll();
        actualizeBullets();
    }

    /**
     * Method that move and decelerate at the same time all the bullets.
     */
    public void moveAllBullets() {
        for(Bullet bullet: battle.getLstBulletsteamA()) {
            moveBullet(bullet);
            decelerateBullet(bullet);
        }
        for(Bullet bullet: battle.getLstBulletsteamB()) {
            moveBullet(bullet);
            decelerateBullet(bullet);
        }
    }

    /**
     * Method that move a Bullet
     * @param bullet {@link Bullet}
     */
    private void moveBullet(Bullet bullet) {
        int xo = (int) bullet.getPosition().getX();
        int yo = (int) bullet.getPosition().getY();
        int x = (int) (Math.cos(bullet.getAngle())*bullet.getSpeed());
        int y = (int) (Math.sin(bullet.getAngle())*bullet.getSpeed());
        bullet.getPosition().move(x+xo, y+yo);
    }

    /**
     * method that reduce the speed of a bullet and increase the counter of it being alive
     * @param bullet {@link Bullet}
     */
    private void decelerateBullet(Bullet bullet) {
        int newSpeed = (int) ( bullet.getSpeed()- Math.pow(GameConfiguration.DEFAULT_BULLET_FRICTION, bullet.getTickAlive()));
        bullet.setTickAlive(bullet.getTickAlive()+1);
        bullet.setSpeed(newSpeed);
    }

    /**
     * Methode that remove all the dead bullets from whom the speed is too slow
     */
    public void actualizeBullets() {
        ArrayList<Bullet> tmpLstBullets = new ArrayList<>();
        tmpLstBullets.addAll(battle.getLstBulletsteamA());
        for(Bullet bullet: tmpLstBullets) {
            bulletKiller(bullet,battle.getLstBulletsteamA());
        }
        tmpLstBullets = new ArrayList<>();
        tmpLstBullets.addAll(battle.getLstBulletsteamB());
        for(Bullet bullet: tmpLstBullets) {
            bulletKiller(bullet,battle.getLstBulletsteamB());
        }
    }

    /**
     * Method that kill Bullet from which the speed is too slow
     * @param bullet {@link Bullet}
     */
    public void bulletKiller(Bullet bullet,ArrayList<Bullet> lstBullet) {
        if(bullet.getSpeed() <= 0.2){
            lstBullet.remove(bullet);
        }
    }



    /**
     * Method that check the collision of the bullet with
     * all the boats in Battle.
     */
    private void collideAll(){
        collideLst(this.battle.getLstBulletsteamA(),this.battle.getBoatsInBattleB());
        collideLst(this.battle.getLstBulletsteamB(),this.battle.getBoatsInBattleA());
    }

    /**
     * Private method that check if a bullet has collided a Boat.
     * @param lstBullets
     * @param fleet
     */
    private void collideLst(ArrayList<Bullet> lstBullets, Fleet fleet) {
        Iterator<Bullet> bulletsIt = lstBullets.iterator();
        Bullet tmp;
        while(bulletsIt.hasNext()) {
            tmp = bulletsIt.next();
            if(collide(tmp, fleet)){
                MapGame.getInstance().addPopUp(new PopUp("explosion", tmp.getPosition(),5));
                bulletsIt.remove();
            }
        }
    }

    /**
     * Methods that check if a bullet has collided with any boat in a Fleet
     * @param bullet
     * @param fleet
     * @return
     */
    private Boolean collide(Bullet bullet, Fleet fleet) {
        Boat tmp = BoatManager.boatCollisionToPoint(bullet.getPosition(), fleet.getArrayListBoat());
        if(tmp != null){
            tmp.addCurrentHp(-GameConfiguration.DAMAGE_PER_BULLET);
            return true;
        }
        return false;
    }
}
