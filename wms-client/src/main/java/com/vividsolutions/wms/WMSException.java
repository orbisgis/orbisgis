package com.vividsolutions.wms;

import java.io.IOException;

public class WMSException extends IOException {
  String source = "";

  public WMSException(String message, String xml_src) {
    super(message);
    this.source=xml_src;
  }

  public String getSource() {
    return source;
  }

}
