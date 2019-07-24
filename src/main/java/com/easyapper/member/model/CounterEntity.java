package com.easyapper.member.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="CounterCollection")
public class CounterEntity {
	
	/**
	 * Collection Name
	 */
	@Id
	private String _id;
	private long seq;
	//Constructor
	public CounterEntity(String _id, long seq) {
		super();
		this._id = _id;
		this.seq = seq;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public long getSeq() {
		return seq;
	}
	public void setSeq(long seq) {
		this.seq = seq;
	}
	@Override
	public String toString() {
		return "CounterEntity [_id=" + _id + ", seq=" + seq + "]";
	}
}
