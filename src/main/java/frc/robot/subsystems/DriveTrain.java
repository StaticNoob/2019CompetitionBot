/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.ConfigParameter;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

/**
 * Add your docs here.
 */
public class DriveTrain extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  // Left motor controllers
  private CANSparkMax left1 = new CANSparkMax(RobotMap.LEFT_DRIVE_1, MotorType.kBrushless);
  private CANSparkMax left2 = new CANSparkMax(RobotMap.LEFT_DRIVE_2, MotorType.kBrushless);
  private CANSparkMax left3 = new CANSparkMax(RobotMap.LEFT_DRIVE_3, MotorType.kBrushless);

  private SpeedControllerGroup leftMotors = new SpeedControllerGroup(left1, left2, left3);

  // Right motor controllers
  private CANSparkMax right1 = new CANSparkMax(RobotMap.RIGHT_DRIVE_1, MotorType.kBrushless);
  private CANSparkMax right2 = new CANSparkMax(RobotMap.RIGHT_DRIVE_2, MotorType.kBrushless);
  private CANSparkMax right3 = new CANSparkMax(RobotMap.RIGHT_DRIVE_3, MotorType.kBrushless);

  private SpeedControllerGroup rightMotors = new SpeedControllerGroup(right1, right2, right3);

  // Drive controller
  private DifferentialDrive drive;

  private CANEncoder leftEncoder = left1.getEncoder();
  private CANEncoder rightEncoder = right1.getEncoder();

  

  // Gyro
  private ADXRS450_Gyro gyro = new ADXRS450_Gyro();

  // Trajectory encoder followers
  private EncoderFollower leftFollower;
  private EncoderFollower rightFollower;

  // Solenoid to control gear shift
  private DoubleSolenoid gearShift = new DoubleSolenoid(4, 3);
  private boolean isFastGear = true;

  // Orientation Swap

  private int reverseDirection = 1;

  public DriveTrain() {
    // left2.follow(left1);
    // left3.follow(left1);

    // right2.follow(right1);
    // right3.follow(right1);

    drive = new DifferentialDrive(leftMotors, rightMotors);
    right2.setInverted(true);
    left3.setInverted(true);

    shiftUp();

    // left1.setParameter(ConfigParameter.kIdleMode, 0);
    // left2.setParameter(ConfigParameter.kIdleMode, 0);
    // left3.setParameter(ConfigParameter.kIdleMode, 0);

    // right1.setParameter(ConfigParameter.kIdleMode, 0);
    // right2.setParameter(ConfigParameter.kIdleMode, 0);
    // right3.setParameter(ConfigParameter.kIdleMode, 0);

    // left1.setParameter(ConfigParameter.kMotorType, 1);
    // left2.setParameter(ConfigParameter.kMotorType, 1);
    // left3.setParameter(ConfigParameter.kMotorType, 1);

    // right1.setParameter(ConfigParameter.kMotorType, 1);
    // right2.setParameter(ConfigParameter.kMotorType, 1);
    // right3.setParameter(ConfigParameter.kMotorType, 1);

    gearShift.set(DoubleSolenoid.Value.kReverse);

    // Set up gyro
    gyro.calibrate();
    // set gearshift

    // Enable drivetrain
    drive.setSafetyEnabled(false);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(null);
  }

  public void trajectoryFollowInit(Trajectory leftTraj, Trajectory rightTraj) {
    leftFollower = new EncoderFollower(leftTraj);
    rightFollower = new EncoderFollower(rightTraj);

    // encoder position, 360 ticks/revolution, 0.1524 m = 6 in wheel diameter
    // right encoder is different: 250 ticks/revolution
    leftFollower.configureEncoder((int) leftEncoder.getPosition(), 360, 0.1524);
    rightFollower.configureEncoder((int) rightEncoder.getPosition(), 250, 0.1524);

    leftFollower.configurePIDVA(1, 0, 0.9, 1 / 2.5, 0);
    rightFollower.configurePIDVA(1, 0, 0.9, 1 / 3.2, 0);
  }

  public void trajectoryFollowRun() {
    // double leftOutput = leftFollower.calculate((int) leftEncoder.getPosition());
    // double rightOutput = rightFollower.calculate((int)
    // rightEncoder.getPosition());

    // double gyro_heading = gyro.getAngle() % 360;
    // double desired_heading = Pathfinder.r2d(leftFollower.getHeading());

    // double angleDifference = Pathfinder.boundHalfDegrees(desired_heading -
    // gyro_heading);
    // double turn = 0.8 * (-1.0/80.0) * angleDifference;
    // double turnTraj = 0;
    // tank(leftOutput + turnTraj, rightOutput - turnTraj);
  }

  public void tank(double left, double right) {
    drive.tankDrive(left * reverseDirection, right * reverseDirection);
  }

  public void arcade(double ySpeed, double zRotation) {
    SmartDashboard.putNumber("left1", left1.getOutputCurrent());
    SmartDashboard.putNumber("left2", left2.getOutputCurrent());
    SmartDashboard.putNumber("left3", left3.getOutputCurrent());
    SmartDashboard.putNumber("right1", right1.getOutputCurrent());
    SmartDashboard.putNumber("right2", right2.getOutputCurrent());
    SmartDashboard.putNumber("right3", right3.getOutputCurrent());
    drive.arcadeDrive(ySpeed * reverseDirection, zRotation * reverseDirection);
  }

  public void shiftUp() {
    gearShift.set(DoubleSolenoid.Value.kForward);
    SmartDashboard.putString("Gear", "Fast");
  }

  public void shiftDown() {
    gearShift.set(DoubleSolenoid.Value.kReverse);
    SmartDashboard.putString("Gear", "Slow");
  }

  public ADXRS450_Gyro getGyro() {
    return gyro;
  }

  public CANEncoder getLeftEncoder() {
    return leftEncoder;
  }

  public CANEncoder getRightEncoder() {
    return rightEncoder;
  }

  public double getAngle() {
    return gyro.getAngle();
  }

  public void reverseDirection() {
    reverseDirection = reverseDirection * -1;
  }
}
