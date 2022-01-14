package com.axxc.replicator.mysql.data;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class BinlogXidData extends BinlogBatchData {
	
	public BinlogXidData(long xid) {
		this.setXid(xid);
	}
}