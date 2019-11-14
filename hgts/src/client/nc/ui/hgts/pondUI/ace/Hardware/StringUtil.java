package nc.ui.hgts.pondUI.ace.Hardware;


public class StringUtil
{
  public static String toHexString(String s)
  {
    String str = "";
    for (int i = 0; i < s.length(); i++)
    {
      int ch = s.charAt(i);
      String s4 = Integer.toHexString(ch);
      str = str + s4;
    }
    return "0x" + str;
  }
  
  public static String toStringHex(String s)
  {
    if ("0x".equals(s.substring(0, 2))) {
      s = s.substring(2);
    }
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      try
      {
        baKeyword[i] = ((byte)(0xFF & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16)));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    try
    {
      s = new String(baKeyword, "utf-8");
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    return s;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String str = "02";
    System.out.print(toStringHex(str));
  }
}
