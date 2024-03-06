package frc.lib.utils;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShuffleBoard {
    ShuffleboardTab tab;

    class Tab {
        public Tab(String tabName) {
            tab = Shuffleboard.getTab(tabName);
        } 
    }

    class Text {

        public Text(String Value) {

        }

    }

    class Number {

    }

}
