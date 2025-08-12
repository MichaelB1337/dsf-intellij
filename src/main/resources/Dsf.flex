package com.example.dsfintellij;

import com.intellij.psi.TokenType;                  // <-- add
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
%state IN_CDATA
%state IN_DSF
%state IN_INLINE

%{
  /** after seeing '<', the next NAME is the tag name */
  private boolean expectTagName = false;
%}

NAME        = [:A-Za-z_][:A-Za-z0-9_\\-\\.]*
UPNAME      = [A-Z][A-Z0-9_]*
WS          = [ \\t]+
NL          = (\\r\\n|\\r|\\n)
DQSTR       = \"([^\"\\\\]|\\\\.)*\"
SQSTR       = \'([^\'\\\\]|\\\\.)*\'
DSF_DIR     = :[A-Z][A-Z0-9_]*
DP_CMD      = \\.dp[a-z][a-z0-9_]*
DOT_CMD     = \\.[a-z][a-z0-9_]*

%%

<YYINITIAL>{
  "<!--" ([^-]|-+[^-])* "-->"  { return COMMENT; }
  "<!DOCTYPE" [^>]* ">"        { return DOCTYPE; }
  "<?xml" [^?]* "\\?>"         { return XML_DECL; }
  "<\\?(?!xml)" [^?]* "\\?>"   { return PI; }
  "<!\\[CDATA\\["              { yybegin(IN_CDATA); return LT; }

  "<" "/"                      { return LT; }   // closing tag starts
  "<"                          { yybegin(IN_TAG); expectTagName = true; return LT; }

  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  "/"                          { return SLASH; }
  "="                          { return EQ; }
  {WS}                         { return TokenType.WHITE_SPACE; }
  {NL}                         { return TokenType.WHITE_SPACE; }

  .                            { return TokenType.WHITE_SPACE; }
}

<IN_TAG>{
  {WS}                         { return TokenType.WHITE_SPACE; }
  "/"                          { return SLASH; }
  ">"                          { yybegin(YYINITIAL); expectTagName = false; return GT; }
  "="                          { return EQ; }
  {NAME}                       { if (expectTagName) { expectTagName = false; return TAG_NAME; } else { return ATTR_NAME; } }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  {NL}                         { return TokenType.WHITE_SPACE; }
  .                            { return TokenType.WHITE_SPACE; }
}

<IN_CDATA>{
  "]]>"                        { yybegin(YYINITIAL); return GT; }
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {NL}                         { return TokenType.WHITE_SPACE; }
  [^]                          { return CDATA_TEXT; }
}

<IN_DSF>{
  {WS}                         { return TokenType.WHITE_SPACE; }
  {UPNAME}                     { return DSF_PARAM_NAME; }
  "="                          { return EQ; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  "\\."                        { yybegin(YYINITIAL); return DSF_DOT; }
  [^\\.\\s]+                   { return DSF_UNQUOTED; }
  {NL}                         { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
}

<IN_INLINE>{
  {WS}                         { return TokenType.WHITE_SPACE; }
  {NL}                         { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
  [^\\n]+                      { yybegin(YYINITIAL); return DSF_INLINE_VALUE; }
}

[^]                            { return BAD_CHAR; }