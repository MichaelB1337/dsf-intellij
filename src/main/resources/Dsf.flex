/* Dsf.flex — IntelliJ JFlex lexer for DSF (XML + DSF commands) */
package dev.michaelbergmann.dsfintellij;

import com.intellij.psi.tree.IElementType;
import static dev.michaelbergmann.dsfintellij.DsfTokenTypes.*;

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
NAME     = [A-Za-z_][A-Za-z0-9_\-\.]*
UPNAME   = [A-Z][A-Z0-9_]*
WS       = [ \t]+
NL       = (\r\n|\r|\n)
DQSTR    = \"([^\"\\]|\\.)*\"
SQSTR    = \'([^\'\\]|\\.)*\'

/* DSF macros */
DSF_DIR  = :[A-Z][A-Z0-9_]*
DP_CMD   = \.dp[a-z][a-z0-9_]*
DOT_CMD  = \.[A-Za-z][A-Za-z0-9_]*

%%

/* =========================
   YYINITIAL (top-level XML)
   ========================= */
<YYINITIAL>{
  "<!--" ([^-]|-+[^-])* "-->"   { return COMMENT; }
  "<!DOCTYPE" [^>]* ">"         { return DOCTYPE; }

  /* XML declaration and PI (note single backslashes for literals) */
  "<\?xml" [^?]* "\?>"          { return XML_DECL; }
  "<\?(?!xml)" [^?]* "\?>"      { return PI; }

  /* CDATA section open */
  "<!\[CDATA\["                 { yybegin(IN_CDATA); return LT; }

  /* Tags */
  "</"                          { yybegin(IN_TAG_NAME); return LT; }
  "<"                           { yybegin(IN_TAG_NAME); return LT; }

  /* DSF (available at top level too) */
  {DSF_DIR}                     { yybegin(IN_DSF);    return DSF_DIRECTIVE; }
  {DP_CMD}                      { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  {WS}                          { return WHITE_SPACE; }
  {NL}                          { return WHITE_SPACE; }
  .                             { return BAD_CHAR; }
}

/* =========================
   IN_TAG_NAME (first NAME after < or </)
   ========================= */
<IN_TAG_NAME>{
  {WS}      { return WHITE_SPACE; }
  {NAME}    { yybegin(IN_TAG); return TAG_NAME; }
  ">"       { yybegin(YYINITIAL); return GT; }
  "/"       { return SLASH; }
  "="       { return EQ; }
  {DQSTR}   { return STRING; }
  {SQSTR}   { return STRING; }
  {NL}      { return WHITE_SPACE; }
  .         { return BAD_CHAR; }
}

/* =========================
   IN_TAG (inside <tag … >)
   ========================= */
<IN_TAG>{
  {WS}      { return WHITE_SPACE; }
  "/"       { return SLASH; }
  ">"       { yybegin(YYINITIAL); return GT; }
  "="       { return EQ; }
  {NAME}    { return ATTR_NAME; }
  {DQSTR}   { return STRING; }
  {SQSTR}   { return STRING; }
  {NL}      { return WHITE_SPACE; }
  .         { return BAD_CHAR; }
}

/* =========================
   IN_CDATA (body of CDATA)
   ========================= */
<IN_CDATA>{
  "\]\]>"   { yybegin(YYINITIAL); return GT; }

  /* DSF inside CDATA */
  {DSF_DIR} { yybegin(IN_DSF);    return DSF_DIRECTIVE; }
  {DP_CMD}  { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD} { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  {NL}      { return WHITE_SPACE; }
  [^]       { return CDATA_TEXT; }
}

/* =========================
   IN_DSF (:DIRECTIVE … .)
   ========================= */
<IN_DSF>{
  {WS}                           { return WHITE_SPACE; }

  // 1) Strings FIRST so '.' inside quotes never terminates the directive
  {DQSTR}                        { return STRING; }
  {SQSTR}                        { return STRING; }

  // 2) Proper param name and '=' as in XML
  {UPNAME}                       { return DSF_PARAM_NAME; }   // e.g. TABLEID, HALIGN
  "="                            { return EQ; }

  // 3) Unquoted chunk: NO dot, NO whitespace/newline, NO quotes, NO '='
  [^\.=\ \t\r\n\'\"]+            { return DSF_UNQUOTED; }

  // 4) A single dot ends the directive (after we've handled strings/unquoted)
  "."                            { yybegin(YYINITIAL); return DSF_DOT; }

  // 5) Safety: newline also ends the directive (rare, but keeps things sane)
  {NL}                           { yybegin(YYINITIAL); return WHITE_SPACE; }
}

/* =========================
   IN_INLINE (.command [args…] until EOL)
   ========================= */
<IN_INLINE>{
  {WS}      { return WHITE_SPACE; }
  {NL}      { yybegin(YYINITIAL); return WHITE_SPACE; }
  [^\r\n]+  { yybegin(YYINITIAL); return DSF_INLINE_VALUE; }
}

/* ---------- Fallback ---------- */
[^]         { return BAD_CHAR; }