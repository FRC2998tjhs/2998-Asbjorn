package frc.robot.subsystems.compresser;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Compresser extends SubsystemBase{
    Compressor m_compressor;

    public Compresser(int CompresserPort) {
        m_compressor = new Compressor(CompresserPort, PneumaticsModuleType.CTREPCM);
    }

    public void Releave() {
        
    }

    public boolean GetEnabled() {
        return m_compressor.isEnabled();
    }
}
