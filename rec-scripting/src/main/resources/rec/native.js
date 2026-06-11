"use strict";

// Scope layout after Scripting installs everything:
//   __rec   = Rec  (action, pred, stateful, wrap + file, flat, cache, restartable, println)
//   __core  = CoreRecPlugin  (csv, counter, collect, target, dummy, stream, stateless, unique)
//   __jdbi  = JdbiModule     (query, resultSet)       — if rec-jdbi on classpath
//   __reactive = ReactiveModule (reactive)             — if rec-reactive on classpath
//   __cache = CacheModule     (inMemory)               — if rec-cache on classpath
//   __jsonl = JsonlModule     (jsonl, jsonlTarget)     — if rec-datatype-jsonl on classpath
//   __parquet = ParquetModule (parquet, parquetTarget) — if rec-datatype-parquet on classpath
//   __agent = AgentModule     (createAgent, chat, addTool, ...) — if rec-agent on classpath

// Build a plain JS object — not a NativeJavaObject — so plugins can add properties.
var rec = {};

// Bridge utilities from Rec
rec.action      = function (fn)      { return __rec.action(fn) };
rec.pred        = function (fn)      { return __rec.pred(fn) };
rec.stateful    = function (obj, fn) { return __rec.stateful(obj, fn) };

// Native methods from Rec
rec.file        = function (f)       { return __rec.file(f) };
rec.flat        = function (f)       { return __rec.flat(f) };
rec.cache       = function (n)       { return __rec.cache(n) };
rec.restartable = function (s)       { return __rec.restartable(s) };
rec.println     = function ()        { __rec.println.apply(__rec, arguments) };

// Core pipeline methods (always present)
rec.csv        = function (r, d, a) { return __core.csv(r, d, a) };
rec.stream     = function (s)       { return __core.stream(s) };
rec.dummy      = function ()        { return __core.dummy() };
rec.target     = function (fn)      { return __core.target(fn) };
rec.counter    = function (fn)      { return __core.counter(fn) };
rec.stateless  = function (fn)      { return __core.stateless(fn) };
rec.collect    = function (c)       { return __core.collect(c) };
rec.unique     = function ()        { return __core.unique.apply(__core, arguments) };

// Plugin methods (present only if module on classpath)
// Use try-catch for reliable detection in Rhino CommonJS modules
try { if (__jdbi) {
    rec.query      = function (u, p, pw, s) { return __jdbi.query(u, p, pw, s) };
    rec.resultSet  = function (rs)           { return __jdbi.resultSet(rs) };
}} catch(e) {}

try { if (__reactive) {
    rec.reactive = function () { return __reactive.reactive() };
}} catch(e) {}

try { if (__cache) {
    rec.inMemoryCache = function (n) { return __cache.inMemory(n) };
}} catch(e) {}

try { if (__jsonl) {
    rec.jsonl      = function (src) { return __jsonl.jsonl(src) };
    rec.jsonlTarget = function (f)  { return __jsonl.jsonlTarget(f) };
}} catch(e) {}

try { if (__parquet) {
    rec.parquet      = function (f) { return __parquet.parquet(f) };
    rec.parquetTarget = function (f) { return __parquet.parquetTarget(f) };
}} catch(e) {}

try { if (__agent) {
    rec.createAgent    = function () { return __agent.createAgent.apply(__agent, arguments) };
    rec.chat           = function (a, m) { return __agent.chat(a, m) };
    rec.addTool        = function (a, n, d, s, fn) { return __agent.addTool(a, n, d, s, fn) };
    rec.addMcpServer   = function (a, c) { return __agent.addMcpServer.apply(__agent, arguments) };
    rec.addSkillDirectories = function (a) { return __agent.addSkillDirectories.apply(__agent, arguments) };
    rec.activateSkill  = function (a, n) { return __agent.activateSkill(a, n) };
    rec.removeBuiltinTool = function (a, n) { return __agent.removeBuiltinTool(a, n) };
    rec.listSkills     = function (a) { return __agent.listSkills(a) };
    rec.listTools      = function (a) { return __agent.listTools(a) };
    rec.close          = function (a) { return __agent.close(a) };
}} catch(e) {}

var mod = require("rec/common").createCommon(rec);

mod.println     = rec.println;
mod.file        = rec.file;
mod.flat        = rec.flat;
mod.cache       = rec.cache;
mod.restartable = rec.restartable;

module.exports = mod;
