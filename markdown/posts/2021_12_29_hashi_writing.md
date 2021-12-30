% Hashicorp Writing Practices and Culture

[link](https://works.hashicorp.com/articles/writing-practices-and-culture)

> we write to clarify our ideas, to articulate concepts to our colleagues both within and beyond our teams, and to gather the feedback we need to make those ideas better. 

# Why write proposals and solution documents?

* Clarify ideas
* articulate concepts to colleagues
* Gather feedback
* Encourage thoughtfulness
* Easy access to context on what a decision is, how it was made
* Ease of operation across timezones
* Equalizing force: change proposal process is same for engineers and founders
* Allows proper credit for ideas
* Allows people less likely to speak up in meetings to be heard
* Avoids long, circular meetings
* Makes decisions more efficient: Come to the meeting having read the doc, yay or nay vote is often enough

# What do they write?

Problem Requirements Document (PRD): Defines the problem. The 'Why'. Request for Comment (RFC): Proposes the solution

# PRD

[PRD Template](https://works.hashicorp.com/articles/prd-template)

* Usually PMs will write the PRD
* Helps team members fully understand a problem and define what's needed to address it.
* Helpful for working through an ambiguous challenge with many stakeholders
* Curating information, not creating it.
* _Before_ a PRD, you should do research.
* Offer a clear problem statement and insight into the problem

> A PRD reader should clearly understand what the problem is, who it affects, and why it is important to solve.

## Template content

1. **Introduction**: Explains the goal of the PRD. Summarizes the content. _Do this last_
2. **Background**: Provide useful context about the problem domain. Context is essential for understanding content. Use visuals if possible
3. **Problem Statements**: The heart of the PRD. Include _Personas_
4. **Phases and requirements**: Break the problem into phases to allow iterative development. 
    * Phases should be independently valuable 
    * Each phase should have one or more requirements / success criteria.
    * 1 sentence description for each phase and each requirement
    * Requirements should have an acceptance criteria, written like test cases. Make as unambiguous as possble
    * Requirements should have considerations section, to be used by the RFC writer. Almost always a question.
    * Each phase should build on the previous ones. 
    * State in decreasing order of value.
5. **User research**: The most important section. Grounds the PRD in real experience. One section for each customer. Include a link to interview notes, and a para summarizing state of workflow and key takeaways.
6. **Approvals**: All stakeholders must approve the PRD before it goes for RFC.

# RFC

[RFC Template](https://works.hashicorp.com/articles/rfc-template)

* Usually Engineers will write the RFC
* Again, articulate and get feedback on solution
* Not _Mandatory_ to have an RFC. Generally for bigger, more complex work, or requiring buy-in from many stakeholders

# Tips for writing and feedback

* Write with a buddy if you haven't written one before
* Collaborate early and often
* Don't worry about it being perfect
* Follow the template. The consistency will make it easier for reviewers to review
* **Don't put too much detail**. Be concise
* Acknowledge and resolve each comment, even those you decide not to act on.
* If feedback seems off, ask questions about what the commenter meant
* Feel free to have brainstorming sessions before writing. The PRD is the artifact, not the process
* For RFC, sometimes an 'options exploration' step after PRD is good.
* If comments are not resolving, get on a Zoom. Easier to do in person.

# Other stuff

[Technical writing workshop](https://drive.google.com/file/d/1zyt0RMCDEwmTEPQQ2yiAxW_NZ1dNOOID/view)
