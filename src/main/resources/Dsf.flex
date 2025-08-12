/* Dsf.flex */
package com.example.dsfintellij;

import com.intellij.psi.tree.IElementType;
import static com.example.dsfintellij.DsfTokenTypes.*;

%%

%public
%class DsfLexer
%implements com.intellij.lexer.FlexLexer
%unicode
%function advance
%type IElementType
%column
%line

%state IN_TAG

NAME   = [:A-Za-z_][:A-Za-z0-9_\-\.]*
WS     = [ \t]+
NL     = (\r\n|\r|\n)
DQSTR  = \"([^\"\\]|\\.)*\"
SQSTR  = \'([^\'\\]|\\.)*\'

%%

/* ===== default ===== */
<YYINITIAL>{
  "<!--" ([^-]|-+[^-])* "-->"  { return COMMENT; }

  "<" "/"                      { return LT; }
  "<" {NAME}                   { yybegin(IN_TAG); return TAG_NAME; }
  {WS}                         { return WHITE_SPACE; }
  {NL}                         { return WHITE_SPACE; }
  .                            { return WHITE_SPACE; }
}

/* ===== inside <tag …> ===== */
<IN_TAG>{
  {WS}                         { return WHITE_SPACE; }
  {NAME}                       { return ATTR_NAME; }
  "="                          { return EQ; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  "/" ">"                      { yybegin(YYINITIAL); return GT; }  // self-close
  ">"                          { yybegin(YYINITIAL); return GT; }
  {NL}                         { return WHITE_SPACE; }
  .                            { return BAD_CHAR; }
}

/* fallback */
[^]                            { return BAD_CHAR; }