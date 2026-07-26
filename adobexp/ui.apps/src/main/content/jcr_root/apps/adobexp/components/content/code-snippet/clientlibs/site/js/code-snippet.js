(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/CodeSnippet/code-snippet.ts
  var require_code_snippet = __commonJS({
    "src/static-components/CodeSnippet/code-snippet.ts"() {
      var prefersReducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)")?.matches ?? false;
      var KEYWORDS = {
        js: [
          "async",
          "await",
          "break",
          "case",
          "catch",
          "class",
          "const",
          "continue",
          "default",
          "delete",
          "do",
          "else",
          "export",
          "extends",
          "finally",
          "for",
          "from",
          "function",
          "if",
          "import",
          "in",
          "instanceof",
          "let",
          "new",
          "of",
          "return",
          "super",
          "switch",
          "this",
          "throw",
          "try",
          "typeof",
          "var",
          "void",
          "while",
          "yield",
          "true",
          "false",
          "null",
          "undefined"
        ],
        java: [
          "abstract",
          "boolean",
          "break",
          "case",
          "catch",
          "class",
          "default",
          "do",
          "double",
          "else",
          "enum",
          "extends",
          "final",
          "finally",
          "float",
          "for",
          "if",
          "implements",
          "import",
          "instanceof",
          "int",
          "interface",
          "long",
          "new",
          "package",
          "private",
          "protected",
          "public",
          "return",
          "static",
          "super",
          "switch",
          "this",
          "throw",
          "throws",
          "try",
          "void",
          "while",
          "true",
          "false",
          "null"
        ],
        sql: [
          "ALTER",
          "AND",
          "AS",
          "ASC",
          "BY",
          "CREATE",
          "DATABASE",
          "DEFAULT",
          "DELETE",
          "DESC",
          "DISTINCT",
          "DROP",
          "ENGINE",
          "EXISTS",
          "FROM",
          "GROUP",
          "HAVING",
          "IF",
          "IN",
          "INSERT",
          "INTERVAL",
          "INTO",
          "JOIN",
          "LIMIT",
          "MATERIALIZED",
          "NOT",
          "NULL",
          "ON",
          "OR",
          "ORDER",
          "PARTITION",
          "SELECT",
          "SET",
          "SETTINGS",
          "TABLE",
          "TTL",
          "UNION",
          "UPDATE",
          "VALUES",
          "WHERE",
          "WITH",
          "CLUSTER",
          "ALIAS"
        ],
        bash: [
          "curl",
          "echo",
          "export",
          "cd",
          "sudo",
          "docker",
          "npm",
          "npx",
          "mvn",
          "git",
          "chmod",
          "mkdir",
          "if",
          "then",
          "fi",
          "for",
          "do",
          "done",
          "clickhouse-client"
        ],
        kotlin: [
          "as",
          "break",
          "by",
          "catch",
          "class",
          "companion",
          "const",
          "continue",
          "data",
          "do",
          "else",
          "enum",
          "false",
          "finally",
          "for",
          "fun",
          "if",
          "import",
          "in",
          "internal",
          "is",
          "lateinit",
          "null",
          "object",
          "override",
          "package",
          "private",
          "return",
          "suspend",
          "this",
          "throw",
          "true",
          "try",
          "val",
          "var",
          "when",
          "while"
        ],
        swift: [
          "as",
          "break",
          "case",
          "catch",
          "class",
          "continue",
          "default",
          "defer",
          "do",
          "else",
          "enum",
          "extension",
          "false",
          "final",
          "for",
          "func",
          "guard",
          "if",
          "import",
          "in",
          "init",
          "internal",
          "let",
          "nil",
          "private",
          "return",
          "self",
          "static",
          "struct",
          "switch",
          "throw",
          "throws",
          "true",
          "try",
          "var",
          "weak",
          "while"
        ],
        python: [
          "and",
          "as",
          "assert",
          "break",
          "class",
          "continue",
          "def",
          "elif",
          "else",
          "except",
          "False",
          "finally",
          "for",
          "from",
          "global",
          "if",
          "import",
          "in",
          "is",
          "lambda",
          "None",
          "not",
          "or",
          "pass",
          "raise",
          "return",
          "True",
          "try",
          "while",
          "with",
          "yield"
        ]
      };
      function escapeHtml(value) {
        return value.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
      }
      var STRING_PATTERN = String.raw`'(?:\\.|[^'\\\n])*'|"(?:\\.|[^"\\\n])*"|` + "`(?:\\\\.|[^`\\\\])*`";
      function commentRules(language) {
        if (language === "bash" || language === "python") {
          return [{ pattern: String.raw`#.*$`, className: "tok-comment" }];
        }
        const block = { pattern: String.raw`/\*[\s\S]*?\*/`, className: "tok-comment" };
        if (language === "sql") {
          return [block, { pattern: String.raw`--.*$`, className: "tok-comment" }];
        }
        return [block, { pattern: String.raw`//.*$`, className: "tok-comment" }];
      }
      function rulesFor(language) {
        if (language === "xml" || language === "html") {
          return [
            { pattern: String.raw`&lt;!--[\s\S]*?--&gt;`, className: "tok-comment" },
            { pattern: String.raw`&lt;/?[\w:.-]+`, className: "tok-tag" },
            { pattern: String.raw`&quot;(?:(?!&quot;)[\s\S])*&quot;|"[^"\n]*"`, className: "tok-str" },
            { pattern: String.raw`[\w:.-]+(?==)`, className: "tok-attr" }
          ];
        }
        if (language === "json") {
          return [
            { pattern: String.raw`"(?:\\.|[^"\\])*"(?=\s*:)`, className: "tok-attr" },
            { pattern: String.raw`"(?:\\.|[^"\\])*"`, className: "tok-str" },
            { pattern: String.raw`\b(?:true|false|null)\b`, className: "tok-key" },
            { pattern: String.raw`-?\b\d+(?:\.\d+)?\b`, className: "tok-num" }
          ];
        }
        const rules = [
          ...commentRules(language),
          { pattern: STRING_PATTERN, className: "tok-str" }
        ];
        const words = KEYWORDS[language];
        if (words) {
          rules.push({ pattern: `\\b(?:${words.join("|")})\\b`, className: "tok-key" });
        }
        rules.push({ pattern: String.raw`\b\d+(?:\.\d+)?\b`, className: "tok-num" });
        return rules;
      }
      function highlight(escaped, language) {
        const rules = rulesFor(language);
        if (rules.length === 0) return escaped;
        const flags = language === "sql" ? "gmi" : "gm";
        const scanner = new RegExp(rules.map((rule) => `(${rule.pattern})`).join("|"), flags);
        let output = "";
        let cursor = 0;
        let match;
        while ((match = scanner.exec(escaped)) !== null) {
          if (match[0] === "") {
            scanner.lastIndex += 1;
            continue;
          }
          const matched = match;
          const ruleIndex = rules.findIndex((_, index) => matched[index + 1] !== void 0);
          if (ruleIndex === -1) continue;
          output += escaped.slice(cursor, match.index);
          output += `<span class="${rules[ruleIndex].className}">${match[0]}</span>`;
          cursor = match.index + match[0].length;
        }
        return output + escaped.slice(cursor);
      }
      function decorate(code) {
        if (code.dataset.highlighted === "true") return;
        const language = (code.dataset.language || "text").toLowerCase();
        const source = code.textContent ?? "";
        code.innerHTML = highlight(escapeHtml(source), language);
        code.dataset.highlighted = "true";
        code.dataset.raw = source;
      }
      function addLineNumbers(pane, code) {
        if (pane.dataset.numbered === "true") return;
        const lines = (code.dataset.raw ?? code.textContent ?? "").replace(/\n$/, "").split("\n").length;
        const gutter = document.createElement("span");
        gutter.className = "code-snippet__gutter";
        gutter.setAttribute("aria-hidden", "true");
        gutter.textContent = Array.from({ length: lines }, (_, i) => String(i + 1)).join("\n");
        pane.querySelector("pre")?.prepend(gutter);
        pane.dataset.numbered = "true";
      }
      function wireCopy(root) {
        const button = root.querySelector(".code-snippet__copy");
        if (!button) return;
        button.addEventListener("click", async () => {
          const active = root.querySelector(".code-snippet__pane.is-active code") ?? root.querySelector(".code-snippet__pane code");
          const text = active?.dataset.raw ?? active?.textContent ?? "";
          if (!text) return;
          try {
            await navigator.clipboard.writeText(text);
            button.classList.add("is-copied");
            button.setAttribute("aria-label", "Copied");
            window.setTimeout(() => {
              button.classList.remove("is-copied");
              button.setAttribute("aria-label", "Copy code");
            }, 1800);
          } catch {
            const range = document.createRange();
            if (active) {
              range.selectNodeContents(active);
              window.getSelection()?.removeAllRanges();
              window.getSelection()?.addRange(range);
            }
          }
        });
      }
      function wireTabs(root) {
        const tabs = Array.from(root.querySelectorAll(".code-snippet__tab"));
        const panes = Array.from(root.querySelectorAll(".code-snippet__pane"));
        if (tabs.length === 0) return;
        const activate = (target) => {
          tabs.forEach((tab) => {
            const active = tab.dataset.target === target;
            tab.classList.toggle("is-active", active);
            tab.setAttribute("aria-selected", String(active));
            tab.tabIndex = active ? 0 : -1;
          });
          panes.forEach((pane) => {
            pane.classList.toggle("is-active", pane.id === target);
            pane.hidden = pane.id !== target;
          });
        };
        tabs.forEach((tab, index) => {
          tab.addEventListener("click", () => activate(tab.dataset.target ?? ""));
          tab.addEventListener("keydown", (event) => {
            if (event.key !== "ArrowRight" && event.key !== "ArrowLeft") return;
            event.preventDefault();
            const offset = event.key === "ArrowRight" ? 1 : -1;
            const next = tabs[(index + offset + tabs.length) % tabs.length];
            next.focus();
            activate(next.dataset.target ?? "");
          });
        });
        const initial = tabs.find((tab) => tab.classList.contains("is-active")) ?? tabs[0];
        activate(initial.dataset.target ?? "");
      }
      (() => {
        const roots = document.querySelectorAll('[data-component="code-snippet"]');
        if (roots.length === 0) return;
        roots.forEach((root) => {
          root.querySelectorAll(".code-snippet__pane").forEach((pane) => {
            const code = pane.querySelector("code");
            if (!code) return;
            decorate(code);
            if (root.dataset.lineNumbers !== "false") addLineNumbers(pane, code);
          });
          wireTabs(root);
          wireCopy(root);
          if (prefersReducedMotion || !("IntersectionObserver" in window)) {
            root.classList.add("is-visible");
            return;
          }
          const observer = new IntersectionObserver(
            (entries) => {
              entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
              });
            },
            { threshold: 0.12, rootMargin: "0px 0px -40px 0px" }
          );
          observer.observe(root);
        });
      })();
    }
  });
  require_code_snippet();
})();
