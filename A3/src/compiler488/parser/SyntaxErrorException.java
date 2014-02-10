package compiler488.parser;

/** Exception subclass for reporting parser syntax errors
 * @author Dave Wortman
 */
public class SyntaxErrorException  extends Exception
{
  public  SyntaxErrorException( String msg )
  {
    super( msg ) ;
  }
}
