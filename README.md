When I interview applicants to software development jobs, I use a specific
assignment to check their readiness to code.

They are to bring a pre-configured programming laptop with them, and after the
discussion part of the interview, develop a simple REST JSON interface using
a toolset of their own choice, whatever they are most comfortable with.

The objective set to the applicant is for them to take the big list of
JSON format country data from
https://github.com/mledoze/countries/blob/master/countries.json and provide
their own HTTP service from which I can get Finland's country information as
response to the HTTP request ``GET /countries/FI HTTP/1.0``.

The point of the assignment is to be able to observe the applicant while they
are working on a typical task. Additionally, to see how internally motivated
they are to complete the task. Typically you get a fairly good idea of the
applicant's level and way of working from the first 20% of the assignment,
so completing isn't really necessary.

If it looks like the applicant knows what they are doing, understands the
principal concepts and is able to explain them and their own strategy for
completing the objective, I often call time well before completion.

One way of grading applicants is to see how comfortable they are on these levels:

1. explaining web services, JSON and REST, as well as their chosen tools

2. asking questions about the assignment, and rewording questions to which
they clearly receive unsatisfactory answers to match the "customer's"
apparent level of comfort with technology

3. explaining related concepts: integrations, scaling, testing, RDBMS, NoSQL

4. explaining lower level concepts: performance, concurrency, networking

5. giving a brief overview of the principles of project management, product
management, system administration and testing

6. selling their favorite concepts or products to a non-technical customer
using credible business-level argumentation

### This is a simple Spring Boot reference implementation of that assignment.

    mvn clean compile spring-boot:run
