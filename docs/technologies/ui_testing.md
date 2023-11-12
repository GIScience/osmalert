# Web Ui Tests

## Playwright

- Supports Google Chrome, Microsoft Edge, Safari (Desktop \& mobile), Firefox
- Tests on Windows, Linux, macOS, locally or on CI
- Compatible with Java
- No race conditions when performing checks
- No error when valid HTTP status code is returned
- Perform actions and assert state against expectations

## Selenium Web Driver

https://www.selenium.dev/documentation/webdriver/

"The Selenium framework ties all of these pieces together through a user-facing
interface that enables the different browser backends to be used transparently,
enabling cross-browser and cross-platform automation."

+ Opensource and free
+ W3C Recommendation
+ Good documentation, large community, large library of plugins/extensions
+ Supports Java
+ Drives a browser natively (either locally or on a remote machine)
+ Supports: Chrome, Edge, Firefox, Safari

- (-)  Setup costs: creating a stable and maintainable test framework need
  time (
  e.g: one need to download a driver for each browser)
- (-) no built-in visual regression testing
- (-) no built-in reporting support (e.g.,external solutions needed to record
  videos)

## Comparison

| Playwright                                                                 | Selenium                                                                |
|----------------------------------------------------------------------------|-------------------------------------------------------------------------|
| 5 times faster                                                             |                                                                         |
| All tests run independently in own worker processes                        | Parallel Execution with Selenium grid (code changes might be necessary) |
| Auto-waiting mechanism<br/>- automatically enabled<br/>- can be configured | Not supported (sleep or wait needed)                                    |
| Automatic test runner (Native playwright tests)                            | Set up test runner (use third party like mock)                          |
| Full report and video animation of test results <br/>- retry included      | No built-in reporting support                                           |
