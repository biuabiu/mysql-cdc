package com.axxc.replicator.mysql.events.listener;
import com.axxc.replicator.mysql.data.*;
import com.github.shyiko.mysql.binlog.event.XidEventData;

public class XidEventDataListener extends AbstractEventListener<XidEventData> {
	
	@Override
	public BinlogBatchData doListening(XidEventData data) {
		return new BinlogXidData(data.getXid());
	}
	
}
