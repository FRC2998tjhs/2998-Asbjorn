package frc.robot.motor;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;

public class LeaderFollower implements Motor {
    private TalonFX leader;
    private TalonFX follower;

    public LeaderFollower(int leader, int follower) {
        this.leader = new TalonFX(leader);
        this.follower = new TalonFX(follower);
        this.follower.setControl(new Follower(leader, false));
    }

    @Override
    public void set(double percentSpeed) {
        leader.set(percentSpeed);
    }
}
