package com.wanniu.game.area;

/**
 * 场景数据
 * 
 * @author agui
 *
 */
public class AreaData {

	public int areaId;
	public String instanceId;
	public float targetX;
	public float targetY;
	public int logicServerId;

	public AreaData(int areaId) {
		this.areaId = areaId;
	}

	public AreaData(int areaId, String instanceId) {
		this.areaId = areaId;
		this.instanceId = instanceId;
	}

	public AreaData(int areaId, float targetX, float targetY) {
		this.areaId = areaId;
		this.targetX = targetX;
		this.targetY = targetY;
	}

	public AreaData(int areaId, float targetX, float targetY, String instanceId) {
		this.areaId = areaId;
		this.targetX = targetX;
		this.targetY = targetY;
		this.instanceId = instanceId;
	}

	public String toString() {
		return new StringBuilder().append(areaId).append(" : ").append(instanceId).append(" x:").append(targetX).append(" y:").append(targetY).toString();
	}

}