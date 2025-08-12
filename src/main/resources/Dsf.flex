/* Dsf.flex — IntelliJ JFlex lexer for DSF (XML + DSF commands) */
package com.example.dsfintellij;

import com.intellij.psi.tree.IElementType;
import static com.example.dsfintellij.DsfTokenTypes.*;  // MUST contain all tokens referenced below

%%

%public
%class DsfLexer
%implements com.intellij.lexer.FlexLexer
%unicode
%function advance
%type IElementType
%column
%line

/* ---------- Lexical states ---------- */
%state IN_TAG_NAME
%state IN_TAG
%state IN_CDATA
%state IN_DSF
%state IN_INLINE

/* ---------- Common macros ---------- */
NAME        = [:A-Za-z_][:A-Za-z0-9_\-\.]*
UPNAME      = [A-Z][A-Z0-9_]*
WS          = [ \t]+
NL          = (\r\n|\r|\n)
DQSTR       = \"([^\"\\]|\\.)*\"
SQSTR       = \'([^\'\\]|\\.)*\'

DSF_DIR     = :[A-Z][A-Z0-9_]*
DP_CMD      = \\.dp[a-z][a-z0-9_]*
DOT_CMD     = \\.([a-z][a-z0-9_]*)  e

%%

/* =========================
   YYINITIAL (top-level XML)
   ========================= */
<YYINITIAL>{

  /* XML comment */
  "<!--" ([^-]|-+[^-])* "-->"  { return COMMENT; }

  /* DOCTYPE */
  "<!DOCTYPE" [^>]* ">"        { return DOCTYPE; }

  /* XML declaration: <?xml … ?> */
  "<\\?xml" [^?]* "\\?>"       { return XML_DECL; }

  /* Other PIs: <? … ?> (but not xml) */
  "<\\?(?!xml)" [^?]* "\\?>"   { return PI; }

  /* CDATA section open */
  "<!\\[CDATA\\["              { yybegin(IN_CDATA); return LT; }

  /* Closing tag open: "</" → go capture tag name next */
  "</"                         { yybegin(IN_TAG_NAME); return LT; }

  /* Opening tag open: "<" → go capture tag name next */
  "<"                          { yybegin(IN_TAG_NAME); return LT; }

  /* DSF directive block starts here; ends on a single '.' */
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }

  /* DSF inline command (including dp…) — then read args (if any) to EOL */
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  {WS}                         { return WHITE_SPACE; }
  {NL}                         { return WHITE_SPACE; }

  .                            { return BAD_CHAR; }
}

/* =========================
   IN_TAG_NAME (first NAME after < or </)
   ========================= */
<IN_TAG_NAME>{
  {WS}                         { /* ignore / let formatter color space */ return WHITE_SPACE; }
  {NAME}                       { yybegin(IN_TAG); return TAG_NAME; }
  ">"                          { yybegin(YYINITIAL); return GT; }
  "/"                          { return SLASH; }
  "="                          { return EQ; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  {NL}                         { return WHITE_SPACE; }
  .                            { return BAD_CHAR; }
}

/* =========================
   IN_TAG (inside <tag … >)
   ========================= */
<IN_TAG>{
  {WS}                         { return WHITE_SPACE; }
  "/"                          { return SLASH; }
  ">"                          { yybegin(YYINITIAL); return GT; }
  "="                          { return EQ; }
  {NAME}                       { return ATTR_NAME; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }
  {NL}                         { return WHITE_SPACE; }
  .                            { return BAD_CHAR; }
}

/* =========================
   IN_CDATA (body of CDATA)
   ========================= */
<IN_CDATA>{
  "]]>"                        { yybegin(YYINITIAL); return GT; }

  /* DSF inside CDATA works the same as at top level */
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  {NL}                         { return WHITE_SPACE; }
  [^]                          { return CDATA_TEXT; }
}

/* =========================
   IN_DSF (:DIRECTIVE … .)
   ========================= */
<IN_DSF>{
  {WS}                         { return WHITE_SPACE; }
  {UPNAME}                     { return DSF_PARAM_NAME; }
  "="                          { return EQ; }
  {DQSTR}                      { return STRING; }
  {SQSTR}                      { return STRING; }

  /* Single dot ends the directive */
  "\\."                        { yybegin(YYINITIAL); return DSF_DOT; }

  /* Unquoted directive value chunk */
  [^\\.\\s]+                   { return DSF_UNQUOTED; }

  {NL}                         { yybegin(YYINITIAL); return WHITE_SPACE; }
}

/* =========================
   IN_INLINE (.command [args…] until EOL)
   ========================= */
<IN_INLINE>{
  {WS}                         { return WHITE_SPACE; }
  {NL}                         { yybegin(YYINITIAL); return WHITE_SPACE; }

  /* Everything else (until EOL) is the inline command value */
  [^\r\n]+                     { yybegin(YYINITIAL); return DSF_INLINE_VALUE; }
}

/* ---------- Fallback ---------- */
[^]                            { return BAD_CHAR; }