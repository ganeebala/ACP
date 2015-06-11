package com.uoa.acpanalysis.wrapper;

import java.util.ArrayList;
import java.util.List;

public class Label {
	String text;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List getStyle() {
		return style;
	}
	public void setStyle(List style) {
		this.style = style;
	}
	List style = new ArrayList();
}
