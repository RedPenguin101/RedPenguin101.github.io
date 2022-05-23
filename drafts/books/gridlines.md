# Gridlines Financial Modeling course

* [Course Link](https://academy.gridlines.com/courses/enrolled/434503)
* [Financial Modeling Handbook](https://www.financialmodellinghandbook.org/)

## Excel
### Setup tips
* Ctrl F1 to hide ribbon
* Minimize formula bar (Ctrl+Shift+u)
* Change "move down on enter" in advanced options
* Untick "edit in cell" in advanced options

### Macros and shortcuts
* [Macros](https://academy.gridlines.com/courses/essential-financial-modelling/lectures/16417899)
* [Productivity Macros](https://www.financialmodellinghandbook.org/financial-modelling-productivity-pack/)
* [Shortcuts PDF](https://cdn.fs.teachablecdn.com/dHCzenYT4OxoCyJCqs8g)
* Formatting
  * `Ctrl+Shift+i` : New input cell
  * `Ctrl+./,` : add/remove decimal places
  * `Ctrl+Shift+.` : factor style (4dp)
  * `Ctrl+Shift+p` : Percent style
  * `Ctrl+Shift+l` : Date style (and cycle date style)
  * `Ctrl+Shift+,` : number style
  * `Ctrl+Shift+c` : clear background
  * `Ctrl+Shift+y` : yellow background
  * `Ctrl+Shift+b` : Black text
  * `Ctrl+Shift+x` : Red text
  * `Ctrl+Shift+m` : Blue text
* Productivity
  * `Ctrl+Shift+a` : Copy formula horizontal
  * `Ctrl+Shift+n` : Row Total (Sum row to right)
  * `Ctrl+c` (copy label), `Ctrl+Shift+q` Create link (use in combo with copy formula `CSa`)
  * `Ctrl+[` follow link, return with `F5 Enter`
  * `Shift+Space` : select row (when row copied, `Shift+Space+Enter` will paste row)
* Review (reviewing someone elses model)
  * `Ctrl+alt+q` : Add model feedback
  * `Ctrl+shift+j` (in review tab) jump to comment
  * `Shift+F2` : Add comment
  * `Ctrl+Alt+P`: Placeholders list

## Core modeling
[Template Spreadsheet](https://cdn.fs.teachablecdn.com/s4cKmx4MTAK63l8zfbSt)

### Why model? What are models for?
* Business analysis lifecycle

```
  Real World    |     Model World
----------------|-----------------------
  Business Q  > | > Conceptual Model
                |         v
                |   Spreadsheet Model
                |         v
Mgmt Insights < | < Analytical Insights
```

### What makes models easy and hard to read
* Do use white-space in your formula
  * `=IF(J47=1,$F45,EOMONTH(I48,$F46))`
  * `= IF(J47 = 1, $F45, EOMONTH(I48, $F46))`
* have long formula
* use off-sheet references in formula
* have daisy-chains (links to links to links)

### Model Structure
* Business writing: Write for the reader, keep things simple. Short clear sentences, BLUF: Bottom Line Up Front
* Excel Workbook: a Book with Chapters. Want to find topics easily, sensible sequence.
* Worksheet: A chapter: Clear labeling of sections, sensible sequence.
* Calculation Block: A paragraph. One idea per paragraph, clarity around points
* Calculations: Sentence: Short

### Workbook level structure
* Left to right logical reading order: inputs->calculations->outputs
* Withing calculations: follow normal flow of Financial Statements order: revenue, operating cost etc.
* Sheet names short (but not too short)
* Use sheet colors for grouping of sheets (eg input sheets)
* Have a template sheet for creating new calculation sheets
* (Quickly create sheet copies with Ctrl+left click and drag on tabs)

### Worksheet (calculation sheet) structure
* ABC: Heading levels, Bold formatting. Visual hierarchy, easy to navigate hierarchy (Ctrl+down)
* D: Markers e.g. 'Less'
* E: Row labels (be specific!)
* F: Constant (e.g. not time-series) Numbers, calcs, that don't relate to a specific time period
* G: Units
* H: Row total
* I: Spacer col, empty/zero vals. Useful for TS periods which need to 'look back' 1 period, so they don't accidentally pick up an erroneous value
* J+: Time series data (hide everything after end of TS)

### Calculation block structure
* Favour many interim rows over complex formula
* One calculation per block, in the last row of the block, preceded by inputs and links to previous blocks
* Use headers
* max 1 Off-sheet references in TS data, preferably 0 (use constant col for Off-sheet links)
* Four types of rows in a calc block: 
  * Input (Note: a local input is useful for testing, but should be replaced with an offsheet link)
  * Offsheet link (Import format: Blue Text). Easy to replicate with `Shift+Space, Ctrl+c` and `Shift+Space+Enter`. **Don't link to a link** (aka daisy chaining). Use hub-linking. (the macro will detect daisy-chaining)

```
A->B->C->D->E BAD

E<-A->B       GOOD
  / \
 D   C
```

  * Onsheet Link. Create with `Ctrl+c, Ctrl+Shift+q, Ctrl+Shift+a`
  * Calculation (at the end)

### Placeholders
* Model building isn't linear, you're not going to get everything you need in the order you want to use it. Use a placeholder
* Color it yellow
* Square Bracket the Label
* Add a comment: why is this placeholder necessary?
* Just hack in the values.
* Make sure you review them: `Ctrl+Alt+P`

### Imports and Exports
* A link from another sheet is an import. Blue text
* The _other side_ is the export. Red text
* Macros take care of this for you.

### Quickchart F11
* Select a row, F11, quickly see the values.
* To compare multiple rows, do a quick chart, select other values ctrl c, back to quickchart, ctrl v

### Modeling balances
* 2 kinds of line items: Flow and Balance
* Examples: Depreciation
* balances: the water tank analogy

```
  ┌──┐
->│  │
  │  │->
  └──┘
```
* The "Corkscrew" calc block: Calculation block with:
  * initial balance (no TS, just constant)
  * Beginning balance
  * Upward Flow 
  * Downward Flow (but remember, always positive convention)
  * Ending balance


## The Solar case study
[Case study PDF](https://cdn.fs.teachablecdn.com/SLHN9G1LQ6eLxu0PbJGI)

* Coinvest in Solar project
* Financial model to evaluate the opportunity, recommendation
* Close date ("COD") 31st March 2021
* 25 year Power Purchase Agreement (PPA) at 6.5 US$c / KWh. invoicing 15 days after ME, payment terms 30 days.
* Sponsors selling 40% of common stock
* plant produces max 250GWh pa (subject to change)
* 97% starting availability, with 0.5% annual degradation
* Seasonals:
  * April-June: 33%
  * July-Sept: 36%
  * Oct-Dec: 15.5%
  * Jan-March: 15.5%
* Operations & Maintenance contractor (O&M) fee of $1.5m, index linked. Indexation annual on COD anniversary. Payment monthly on 30 day terms
* 15% tax on income
* 3% WHT on div and interest
* Project costs by COD are $100m
* Senior debt with 3.8% margin (act/360). Int+prin pay Qly. LTV is ~70%
* 2.5% IRS for senior debt term.
* Revolver available at 12%
* Equity will fund 30% of cost not covered by debt.

### 5 Timing flags
1. One-off flags: a specific event occurs
2. Periods before specific event
3. Periods after specific event
4. Periods inbetween 2 dates (debt repayment period)
5. Recurring event: every quarter, every 6 months. Tax payment, interest payment period

e.g. calc block for one-off flag:
* Event Date constant row
* Model period end in-sheet link row
* Flag row: `if model-period-end = constant, 1 else 0`. Remember to put row-total
* can also have 'occurs in a period'. have a mode period end and beginning rows, and flag row formula will be `if model-period-start <= constant <= model-period-end, 1, 0`
* For type 4: Event date constant, duration constant, 2nd event date. 
* For type 5: Month Number calc row, `month(J10)`, payment month constant, flag: `if payment-month = constant, 1, 0`. For quarterly, have multiple constants, `if(isna(match(month-number, [constants-array],0])),0,1)`

### Three-way financial model
* Linked Income statement, cash flow statement, balance sheet.


## Stuff a non-excel model would need to do
* Have the hierarchical stuff: workbook, sheet, block, calc
* Have live-updating spreadsheet view
