/* Dsf.flex - JFlex lexer for DSF (XML-ish + DSF commands) */
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
%state IN_CDATA
%state IN_DSF
%state IN_INLINE

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

/* =========================
   YYINITIAL (default state)
   ========================= */

<YYINITIAL>{

  "<!--" ([^-]|-+[^-])* "-->"  { return COMMENT; }

  "<!DOCTYPE" [^>]* ">"        { return DOCTYPE; }

  "<?xml" [^?]* "\\?>"         { return XML_DECL; }

  "<\\?(?!xml)" [^?]* "\\?>"   { return PI; }

  "<!\\[CDATA\\["              { yybegin(IN_CDATA); return LT; }

  // Tag starts
  "<" "/"                      { return LT; }
  "<"                          { yybegin(IN_TAG); return LT; }

  // DSF directives: :NAME ... .
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }

  // Inline commands (two kinds)
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    {
                                  // emulate negative lookahead (not starting with ".dp")
                                  if (yytext().length() >= 3 &&
                                      yytext().charAt(1) == 'd' &&
                                      yytext().charAt(2) == 'p') {
                                      // this is actually .dp..., treat like DP_CMD branch
                                      yybegin(IN_INLINE); return DSF_INLINE_CMD;
                                  } else {
                                      yybegin(IN_INLINE); return DSF_INLINE_CMD;
                                  }
                               }

  "/"                          { return SLASH; }
  "="                          { return EQ; }
  {WS}                         { return WHITE_SPACE; }
  {NL}                         { return WHITE_SPACE; }

  .                            { return WHITE_SPACE; }
}

/* ====== IN_TAG ====== */
<IN_TAG>{
  {WS}                         { return WHITE_SPACE; }
  "/"                          { return SLASH; }
  ">"                          { yybegin(YYINITIAL); return GT; }
  "="                          { return EQ; }
  {NAME}                       { return ATTR_NAME; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  {NL}                         { return WHITE_SPACE; }
  .                            { return WHITE_SPACE; }
}

/* ====== IN_CDATA ====== */
<IN_CDATA>{
  "]]>"                        { yybegin(YYINITIAL); return GT; }
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    {
                                  if (yytext().length() >= 3 &&
                                      yytext().charAt(1) == 'd' &&
                                      yytext().charAt(2) == 'p') {
                                      yybegin(IN_INLINE); return DSF_INLINE_CMD;
                                  } else {
                                      yybegin(IN_INLINE); return DSF_INLINE_CMD;
                                  }
                               }
  {NL}                         { return WHITE_SPACE; }
  [^]                          { return CDATA_TEXT; }
}

/* ====== IN_DSF (directive body) ====== */
<IN_DSF>{
  {WS}                         { return WHITE_SPACE; }
  {UPNAME}                     { return DSF_PARAM_NAME; }
  "="                          { return EQ; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  "\\."                        { yybegin(YYINITIAL); return DSF_DOT; }
  [^\\.\\s]+                   { return DSF_UNQUOTED; }
  {NL}                         { yybegin(YYINITIAL); return WHITE_SPACE; }
}

/* ====== IN_INLINE (after '.dp...' or '.foo') ====== */
<IN_INLINE>{
  {WS}                         { return WHITE_SPACE; }
  {NL}                         { yybegin(YYINITIAL); return WHITE_SPACE; }
  [^\\n]+                      { yybegin(YYINITIAL); return DSF_INLINE_VALUE; }
}

/* Fallback */
[^]                            { return BAD_CHAR; }