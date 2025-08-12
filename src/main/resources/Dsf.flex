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
DOT_CMD     = \\.(?!dp)[a-z][a-z0-9_]*

%%

/* =========================
   YYINITIAL (default state)
   ========================= */

<YYINITIAL>{
  "<!--"                { yybegin(YYINITIAL); return COMMENT; }  // consume in one go below
}

<YYINITIAL>{
  "<!--" ([^-]|-+[^-])* "-->"  { return COMMENT; }

  "<!DOCTYPE" [^>]* ">"        { return DOCTYPE; }

  "<?xml" [^?]* "\\?>"         { return XML_DECL; }

  "<\\?(?!xml)" [^?]* "\\?>"   { return PI; }

  "<!\\[CDATA\\["              { yybegin(IN_CDATA); return LT; }  // style like punctuation begin
  // Tags
  "<" "/"                      { return LT; }                     // '<' part; '/' will be separate rule below if needed
  "<"                          { yybegin(IN_TAG); return LT; }

  // DSF directives (block): :NAME ... .
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }

  // Inline commands: .dp... OR .foo...
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }

  // plain slash in text
  "/"                          { return SLASH; }
  "="                          { return EQ; }
  {WS}                         { return WHITE_SPACE; }
  {NL}                         { return WHITE_SPACE; }

  .                            { return DsfTokenTypes.WHITE_SPACE; } // treat other text as plain
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
  .                            { return DsfTokenTypes.WHITE_SPACE; }
}

/* ====== IN_CDATA ====== */
<IN_CDATA>{
  "]]>"                        { yybegin(YYINITIAL); return GT; }   // treat like end '>'
  {DSF_DIR}                    { yybegin(IN_DSF); return DSF_DIRECTIVE; }
  {DP_CMD}                     { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {DOT_CMD}                    { yybegin(IN_INLINE); return DSF_INLINE_CMD; }
  {NL}                         { return WHITE_SPACE; }
  [^]                          { return CDATA_TEXT; } // any char including > etc.
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