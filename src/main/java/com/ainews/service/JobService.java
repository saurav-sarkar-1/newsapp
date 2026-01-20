package com.ainews.service;

import com.ainews.model.JobPosting;
import com.ainews.repository.CommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    // Adzuna API for job search (free tier available)
    @Value("${adzuna.api.id:${ADZUNA_API_ID:}}")
    private String adzunaApiId;

    @Value("${adzuna.api.key:${ADZUNA_API_KEY:}}")
    private String adzunaApiKey;

    // GitHub Jobs API (free, no key required)
    private static final String GITHUB_JOBS_API = "https://jobs.github.com/positions.json";

    // RemoteOK API (free, no key required)
    private static final String REMOTEOK_API = "https://remoteok.com/api";

    public JobService() {
        this.webClient = WebClient.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(5 * 1024 * 1024)) // 5MB buffer (increased from default 256KB)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetch English job postings in Germany from multiple sources
     */
    public List<JobPosting> getEnglishJobsInGermany() {
        List<JobPosting> allJobs = new ArrayList<>();

        System.out.println("========================================");
        System.out.println("Starting job fetch from multiple sources");
        System.out.println("========================================");

        // Try fetching from multiple sources
        try {
            // Source 1: Adzuna API (if configured)
            if (adzunaApiId != null && !adzunaApiId.isEmpty()) {
                System.out.println("Adzuna API ID configured: " + adzunaApiId);
                System.out.println("Fetching from Adzuna API...");
                List<JobPosting> adzunaJobs = fetchFromAdzuna();
                if (adzunaJobs != null && !adzunaJobs.isEmpty()) {
                    System.out.println("✅ Successfully fetched " + adzunaJobs.size() + " jobs from Adzuna");
                    allJobs.addAll(adzunaJobs);
                } else {
                    System.out.println("❌ Adzuna returned no jobs");
                }
            } else {
                System.out.println("⚠️  Adzuna API not configured (ID: " + adzunaApiId + ")");
            }

            // Source 2: RemoteOK API
            System.out.println("\nFetching from RemoteOK API...");
            List<JobPosting> remoteOkJobs = fetchFromRemoteOK();
            if (remoteOkJobs != null && !remoteOkJobs.isEmpty()) {
                System.out.println("✅ Successfully fetched " + remoteOkJobs.size() + " jobs from RemoteOK");
                allJobs.addAll(remoteOkJobs);
            } else {
                System.out.println("❌ RemoteOK returned no jobs");
            }

        } catch (Exception e) {
            System.err.println("❌ Error fetching jobs: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("Total jobs fetched from APIs: " + allJobs.size());
        System.out.println("========================================\n");

        // If no jobs fetched from APIs, return sample data
//        if (allJobs.isEmpty()) {
//            System.out.println("⚠️  No jobs from APIs, using sample job data");
//            return getSampleJobs();
//        }

        // Load comments for each job
        for (JobPosting job : allJobs) {
            String savedComment = commentRepository.getComment(job.getUrl());
            job.setComments(savedComment != null ? savedComment : "");
        }

        return allJobs;
    }

    /**
     * Fetch jobs from Adzuna API
     * API Docs: https://developer.adzuna.com/
     */
    private List<JobPosting> fetchFromAdzuna() {
        try {
            // Broader search terms for more results - including data entry, admin, banking, education
            String searchTerms = "english OR data entry OR administrative OR banking OR teacher OR office";
            String url = String.format(
                "https://api.adzuna.com/v1/api/jobs/de/search/1?app_id=%s&app_key=%s&results_per_page=50&what=%s&content-type=application/json",
                adzunaApiId, adzunaApiKey, java.net.URLEncoder.encode(searchTerms, "UTF-8")
            );

            System.out.println("Fetching from Adzuna API...");
            System.out.println("API URL (without credentials): https://api.adzuna.com/v1/api/jobs/de/search/1?results_per_page=50&what=" + searchTerms);

            String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(15))
                .block();

            if (response == null || response.isEmpty()) {
                System.err.println("Adzuna API returned empty response");
                return null;
            }

            System.out.println("Adzuna API response received, length: " + response.length());
            List<JobPosting> jobs = parseAdzunaResponse(response);
            System.out.println("Adzuna API returned " + (jobs != null ? jobs.size() : 0) + " jobs");
            return jobs;
        } catch (Exception e) {
            System.err.println("Adzuna API error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetch jobs from RemoteOK API
     * API Docs: https://remoteok.com/api
     */
    private List<JobPosting> fetchFromRemoteOK() {
        try {
            System.out.println("Attempting to fetch from RemoteOK API: " + REMOTEOK_API);

            String response = webClient.get()
                .uri(REMOTEOK_API)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(15))
                .block();

            if (response == null || response.isEmpty()) {
                System.err.println("RemoteOK API returned empty response");
                return null;
            }

            System.out.println("RemoteOK API response received, length: " + response.length());
            return parseRemoteOKResponse(response);
        } catch (org.springframework.core.io.buffer.DataBufferLimitException e) {
            System.err.println("RemoteOK API error: Response too large (exceeded buffer limit)");
            System.err.println("Note: RemoteOK returns a very large dataset. This is expected and the app will use other sources.");
            return null;
        } catch (Exception e) {
            System.err.println("RemoteOK API error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("buffer")) {
                System.err.println("Note: Response size issue - falling back to other job sources");
            }
            return null;
        }
    }

    /**
     * Fetch jobs from GitHub Jobs API
     * Note: GitHub Jobs was shut down in 2021, but keeping for reference
     */
    private List<JobPosting> fetchFromGitHub() {
        try {
            String url = GITHUB_JOBS_API + "?location=germany&description=english";

            String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseGitHubResponse(response);
        } catch (Exception e) {
            System.err.println("GitHub Jobs API error (API deprecated): " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse Adzuna API response
     */
    private List<JobPosting> parseAdzunaResponse(String jsonResponse) {
        List<JobPosting> jobs = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Check for API errors
            if (root.has("error")) {
                System.err.println("Adzuna API error: " + root.get("error").asText());
                return jobs;
            }

            JsonNode resultsNode = root.get("results");

            if (resultsNode != null && resultsNode.isArray()) {
                System.out.println("Adzuna returned " + resultsNode.size() + " results");

                for (JsonNode jobNode : resultsNode) {
                    try {
                        JobPosting job = new JobPosting();
                        job.setTitle(jobNode.path("title").asText("No title"));
                        job.setCompany(jobNode.path("company").path("display_name").asText("Unknown Company"));
                        job.setLocation(jobNode.path("location").path("display_name").asText("Germany"));
                        job.setDescription(jobNode.path("description").asText(""));
                        job.setUrl(jobNode.path("redirect_url").asText(""));
                        job.setSalary(formatSalary(jobNode));
                        job.setJobType(jobNode.path("contract_time").asText("Full-time"));
                        job.setSource("Adzuna");
                        job.setPostedAt(parseDate(jobNode.path("created").asText("")));
                        job.setCategory(categorizeJob(job));

                        jobs.add(job);
                    } catch (Exception e) {
                        System.err.println("Error parsing individual Adzuna job: " + e.getMessage());
                    }
                }
                System.out.println("Successfully parsed " + jobs.size() + " jobs from Adzuna");
            } else {
                System.err.println("Adzuna response has no results array");
            }
        } catch (Exception e) {
            System.err.println("Error parsing Adzuna response: " + e.getMessage());
            e.printStackTrace();
        }
        return jobs;
    }

    /**
     * Parse RemoteOK API response
     */
    private List<JobPosting> parseRemoteOKResponse(String jsonResponse) {
        List<JobPosting> jobs = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.isArray()) {
                System.out.println("RemoteOK returned " + root.size() + " total results");
                int germanJobs = 0;

                for (JsonNode jobNode : root) {
                    // Skip the first element if it's metadata (RemoteOK API quirk)
                    if (!jobNode.has("position")) {
                        continue;
                    }

                    // Filter for Germany-related jobs
                    String location = jobNode.path("location").asText("").toLowerCase();
                    if (!location.contains("germany") && !location.contains("berlin") &&
                        !location.contains("munich") && !location.contains("hamburg") &&
                        !location.contains("frankfurt") && !location.contains("cologne")) {
                        continue;
                    }

                    try {
                        JobPosting job = new JobPosting();
                        job.setTitle(jobNode.path("position").asText("No title"));
                        job.setCompany(jobNode.path("company").asText("Unknown Company"));
                        job.setLocation(jobNode.path("location").asText("Remote - Germany"));
                        job.setDescription(jobNode.path("description").asText(""));
                        job.setUrl(jobNode.path("url").asText(""));
                        job.setJobType("Remote");
                        job.setSource("RemoteOK");
                        job.setLogoUrl(jobNode.path("company_logo").asText(""));

                        // Parse tags as skills
                        JsonNode tagsNode = jobNode.path("tags");
                        if (tagsNode.isArray()) {
                            List<String> skillsList = new ArrayList<>();
                            for (JsonNode tag : tagsNode) {
                                skillsList.add(tag.asText());
                            }
                            job.setSkills(skillsList.toArray(new String[0]));
                        }

                        job.setPostedAt(LocalDateTime.now().minusDays((int)(Math.random() * 7)));
                        job.setCategory(categorizeJob(job));

                        jobs.add(job);
                        germanJobs++;

                        if (jobs.size() >= 15) break; // Limit to 15 jobs from RemoteOK
                    } catch (Exception e) {
                        System.err.println("Error parsing individual RemoteOK job: " + e.getMessage());
                    }
                }
                System.out.println("Found " + germanJobs + " Germany-related jobs from RemoteOK");
            } else {
                System.err.println("RemoteOK response is not an array");
            }
        } catch (Exception e) {
            System.err.println("Error parsing RemoteOK response: " + e.getMessage());
            e.printStackTrace();
        }
        return jobs;
    }

    /**
     * Parse GitHub Jobs API response
     */
    private List<JobPosting> parseGitHubResponse(String jsonResponse) {
        List<JobPosting> jobs = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.isArray()) {
                for (JsonNode jobNode : root) {
                    JobPosting job = new JobPosting();
                    job.setTitle(jobNode.path("title").asText("No title"));
                    job.setCompany(jobNode.path("company").asText("Unknown Company"));
                    job.setLocation(jobNode.path("location").asText("Germany"));
                    job.setDescription(jobNode.path("description").asText(""));
                    job.setUrl(jobNode.path("url").asText(""));
                    job.setJobType(jobNode.path("type").asText("Full-time"));
                    job.setSource("GitHub Jobs");
                    job.setCompany(jobNode.path("company").asText(""));
                    job.setLogoUrl(jobNode.path("company_logo").asText(""));
                    job.setPostedAt(parseDate(jobNode.path("created_at").asText("")));
                    job.setCategory(categorizeJob(job));

                    jobs.add(job);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing GitHub response: " + e.getMessage());
        }
        return jobs;
    }

    /**
     * Format salary information
     */
    private String formatSalary(JsonNode jobNode) {
        double salaryMin = jobNode.path("salary_min").asDouble(0);
        double salaryMax = jobNode.path("salary_max").asDouble(0);

        if (salaryMin > 0 && salaryMax > 0) {
            return String.format("€%.0fk - €%.0fk", salaryMin / 1000, salaryMax / 1000);
        } else if (salaryMin > 0) {
            return String.format("From €%.0fk", salaryMin / 1000);
        }
        return "Competitive";
    }

    /**
     * Parse date string to LocalDateTime
     */
    private LocalDateTime parseDate(String dateString) {
        try {
            if (dateString.isEmpty()) {
                return LocalDateTime.now();
            }
            return LocalDateTime.parse(dateString.substring(0, 19),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * Categorize job based on title and description
     */
    private String categorizeJob(JobPosting job) {
        String text = (job.getTitle() + " " + job.getDescription()).toLowerCase();

        if (text.matches(".*(data entry|data processing|clerk|typing|entry specialist).*")) {
            return "Data Entry";
        } else if (text.matches(".*(administrative|assistant|office manager|secretary|receptionist|coordinator).*")) {
            return "Administrative";
        } else if (text.matches(".*(bank|banking|finance|financial|investment|compliance|loan|credit).*")) {
            return "Banking";
        } else if (text.matches(".*(teacher|teaching|education|school|university|academic|counselor|tutor|instructor).*")) {
            return "Education";
        } else if (text.matches(".*(software|developer|engineer|backend|frontend|fullstack|full-stack).*")) {
            return "Software Development";
        } else if (text.matches(".*(data scientist|analyst|scientist|machine learning|ai|artificial intelligence).*")) {
            return "Data & AI";
        } else if (text.matches(".*(devops|cloud|infrastructure|aws|azure|gcp).*")) {
            return "DevOps & Cloud";
        } else if (text.matches(".*(product|manager|agile|scrum).*")) {
            return "Product Management";
        } else if (text.matches(".*(design|ux|ui|designer).*")) {
            return "Design";
        } else if (text.matches(".*(qa|test|quality).*")) {
            return "QA & Testing";
        }

        return "General";
    }

    /**
     * Sample job postings for English jobs in Germany
     */
    private List<JobPosting> getSampleJobs() {
        List<JobPosting> jobs = new ArrayList<>();

        JobPosting job1 = new JobPosting(
            "Senior Software Engineer - Backend (Java/Spring Boot)",
            "SAP SE",
            "Berlin, Germany",
            "Join our team to build next-generation cloud applications. We're looking for experienced Java developers to work on microservices architecture. English is our company language. Requirements: 5+ years Java experience, Spring Boot, REST APIs, Docker, Kubernetes.",
            "https://www.sap.com/careers",
            "Full-time",
            "Senior",
            "€70k - €95k",
            LocalDateTime.now().minusDays(2),
            "SAP Careers"
        );
        job1.setCategory("Software Development");
        job1.setSkills(new String[]{"Java", "Spring Boot", "Kubernetes", "Docker", "REST API"});

        JobPosting job2 = new JobPosting(
            "Frontend Developer (React/TypeScript)",
            "Zalando SE",
            "Berlin, Germany",
            "Build amazing shopping experiences for millions of users. Work with modern frontend technologies in an international team. English-speaking environment. Requirements: React, TypeScript, CSS, responsive design.",
            "https://jobs.zalando.com",
            "Full-time",
            "Mid",
            "€55k - €75k",
            LocalDateTime.now().minusDays(5),
            "Zalando Jobs"
        );
        job2.setCategory("Software Development");
        job2.setSkills(new String[]{"React", "TypeScript", "CSS", "JavaScript"});

        JobPosting job3 = new JobPosting(
            "Data Scientist - Machine Learning",
            "Siemens AG",
            "Munich, Germany",
            "Apply ML techniques to industrial IoT data. Work on predictive maintenance and optimization. International team, English as working language. Requirements: Python, TensorFlow, PyTorch, ML algorithms.",
            "https://www.siemens.com/careers",
            "Full-time",
            "Mid-Senior",
            "€65k - €85k",
            LocalDateTime.now().minusDays(1),
            "Siemens Careers"
        );
        job3.setCategory("Data & AI");
        job3.setSkills(new String[]{"Python", "Machine Learning", "TensorFlow", "PyTorch"});

        JobPosting job4 = new JobPosting(
            "DevOps Engineer (AWS/Terraform)",
            "N26 Bank",
            "Berlin, Germany",
            "Scale our banking infrastructure to millions of users. Work with cutting-edge cloud technologies. Fully English-speaking team. Requirements: AWS, Terraform, Kubernetes, CI/CD, Python.",
            "https://n26.com/careers",
            "Full-time",
            "Mid",
            "€60k - €80k",
            LocalDateTime.now().minusDays(3),
            "N26 Careers"
        );
        job4.setCategory("DevOps & Cloud");
        job4.setSkills(new String[]{"AWS", "Terraform", "Kubernetes", "CI/CD", "Python"});

        JobPosting job5 = new JobPosting(
            "Full Stack Developer (Node.js/React)",
            "FlixBus",
            "Munich, Germany",
            "Build the future of travel technology. Work on our booking platform serving millions. English as company language. Requirements: Node.js, React, MongoDB, RESTful APIs.",
            "https://www.flixbus.com/careers",
            "Full-time",
            "Mid",
            "€50k - €70k",
            LocalDateTime.now().minusDays(4),
            "FlixBus Careers"
        );
        job5.setCategory("Software Development");
        job5.setSkills(new String[]{"Node.js", "React", "MongoDB", "REST API"});

        JobPosting job6 = new JobPosting(
            "Product Manager - Digital Products",
            "Delivery Hero SE",
            "Berlin, Germany",
            "Lead product development for our food delivery platform. International environment, English required. Requirements: 3+ years PM experience, Agile, data-driven decision making.",
            "https://www.deliveryhero.com/careers",
            "Full-time",
            "Mid-Senior",
            "€60k - €85k",
            LocalDateTime.now().minusDays(6),
            "Delivery Hero Careers"
        );
        job6.setCategory("Product Management");
        job6.setSkills(new String[]{"Product Management", "Agile", "Scrum", "Analytics"});

        JobPosting job7 = new JobPosting(
            "Cloud Architect (Azure/GCP)",
            "BMW Group",
            "Munich, Germany",
            "Design cloud infrastructure for automotive solutions. Work on next-gen connected car platform. English-speaking team. Requirements: Azure/GCP, microservices, security.",
            "https://www.bmwgroup.jobs",
            "Full-time",
            "Senior",
            "€75k - €100k",
            LocalDateTime.now().minusHours(12),
            "BMW Careers"
        );
        job7.setCategory("DevOps & Cloud");
        job7.setSkills(new String[]{"Azure", "GCP", "Cloud Architecture", "Microservices"});

        JobPosting job8 = new JobPosting(
            "AI/ML Engineer - NLP",
            "Bosch",
            "Stuttgart, Germany (Remote possible)",
            "Develop NLP solutions for industrial applications. International R&D team. English required. Requirements: Python, NLP, transformers, BERT, GPT.",
            "https://www.bosch.com/careers",
            "Full-time",
            "Senior",
            "€70k - €90k",
            LocalDateTime.now().minusDays(2),
            "Bosch Careers"
        );
        job8.setCategory("Data & AI");
        job8.setSkills(new String[]{"Python", "NLP", "Machine Learning", "Deep Learning"});

        JobPosting job9 = new JobPosting(
            "QA Automation Engineer (Selenium/Cypress)",
            "TeamViewer",
            "Göppingen, Germany (Remote)",
            "Build automated testing infrastructure. Remote-friendly, English-speaking team. Requirements: Selenium, Cypress, JavaScript, CI/CD pipelines.",
            "https://www.teamviewer.com/careers",
            "Full-time",
            "Mid",
            "€50k - €65k",
            LocalDateTime.now().minusDays(7),
            "TeamViewer Careers"
        );
        job9.setCategory("QA & Testing");
        job9.setSkills(new String[]{"Selenium", "Cypress", "Test Automation", "JavaScript"});

        JobPosting job10 = new JobPosting(
            "UX/UI Designer",
            "SoundCloud",
            "Berlin, Germany",
            "Design intuitive music streaming experiences. Creative international team. English as working language. Requirements: Figma, user research, prototyping.",
            "https://soundcloud.com/jobs",
            "Full-time",
            "Mid",
            "€55k - €70k",
            LocalDateTime.now().minusDays(3),
            "SoundCloud Jobs"
        );
        job10.setCategory("Design");
        job10.setSkills(new String[]{"Figma", "UI Design", "UX Research", "Prototyping"});

        jobs.add(job1);
        jobs.add(job2);
        jobs.add(job3);
        jobs.add(job4);
        jobs.add(job5);
        jobs.add(job6);
        jobs.add(job7);
        jobs.add(job8);
        jobs.add(job9);
        jobs.add(job10);

        // Data Entry Jobs
        JobPosting job11 = new JobPosting(
            "Data Entry Specialist - English Speaker",
            "Deutsche Post DHL",
            "Frankfurt, Germany",
            "Join our international logistics team as a Data Entry Specialist. Process shipping documentation, customer data, and logistics information. English fluency required. No German necessary. Requirements: Accurate typing, MS Office, attention to detail.",
            "https://careers.dhl.com",
            "Full-time",
            "Entry",
            "€30k - €38k",
            LocalDateTime.now().minusDays(2),
            "DHL Careers"
        );
        job11.setCategory("Data Entry");
        job11.setSkills(new String[]{"Data Entry", "MS Office", "Excel", "Typing", "Attention to Detail"});

        JobPosting job12 = new JobPosting(
            "Data Processing Clerk (English)",
            "Lufthansa Group",
            "Munich, Germany",
            "Process flight and passenger data for international operations. English-speaking role within our international team. Requirements: Data entry experience, computer literacy, organized work style.",
            "https://www.be-lufthansa.com/",
            "Full-time",
            "Entry",
            "€32k - €40k",
            LocalDateTime.now().minusDays(4),
            "Lufthansa Careers"
        );
        job12.setCategory("Data Entry");
        job12.setSkills(new String[]{"Data Processing", "MS Office", "Organizational Skills", "English"});

        // Administrative Jobs
        JobPosting job13 = new JobPosting(
            "Administrative Assistant - International Office",
            "Siemens Healthineers",
            "Erlangen, Germany",
            "Support our international medical technology team. Handle correspondence, scheduling, travel arrangements. English as working language. Requirements: Administrative experience, MS Office, organizational skills.",
            "https://www.siemens-healthineers.com/careers",
            "Full-time",
            "Mid",
            "€38k - €48k",
            LocalDateTime.now().minusDays(1),
            "Siemens Healthineers"
        );
        job13.setCategory("Administrative");
        job13.setSkills(new String[]{"Administration", "MS Office", "Scheduling", "Communication", "English"});

        JobPosting job14 = new JobPosting(
            "Office Manager - English Speaking Environment",
            "Rocket Internet",
            "Berlin, Germany",
            "Manage daily office operations for our startup incubator. English-only environment. Coordinate facilities, vendors, events. Requirements: Office management experience, multitasking, people skills.",
            "https://www.rocket-internet.com/careers",
            "Full-time",
            "Mid",
            "€40k - €52k",
            LocalDateTime.now().minusDays(3),
            "Rocket Internet Careers"
        );
        job14.setCategory("Administrative");
        job14.setSkills(new String[]{"Office Management", "Event Planning", "Vendor Management", "Multitasking"});

        JobPosting job15 = new JobPosting(
            "Executive Assistant to CEO (English)",
            "Auto1 Group",
            "Berlin, Germany",
            "Support C-level executives in our international automotive marketplace. Manage calendars, correspondence, travel. English fluency essential. Requirements: Executive support experience, discretion, excellent organization.",
            "https://www.auto1-group.com/careers",
            "Full-time",
            "Senior",
            "€45k - €60k",
            LocalDateTime.now().minusHours(18),
            "Auto1 Careers"
        );
        job15.setCategory("Administrative");
        job15.setSkills(new String[]{"Executive Support", "Calendar Management", "Travel Planning", "Confidentiality"});

        // Bank Jobs
        JobPosting job16 = new JobPosting(
            "Customer Service Representative - International Banking",
            "Deutsche Bank",
            "Frankfurt, Germany",
            "Serve international banking clients in English. Handle inquiries, transactions, account management. English-speaking team. Requirements: Banking/finance background, customer service skills, financial products knowledge.",
            "https://www.db.com/careers",
            "Full-time",
            "Mid",
            "€42k - €55k",
            LocalDateTime.now().minusDays(2),
            "Deutsche Bank Careers"
        );
        job16.setCategory("Banking");
        job16.setSkills(new String[]{"Customer Service", "Banking", "Financial Products", "English", "Communication"});

        JobPosting job17 = new JobPosting(
            "Junior Investment Analyst (English)",
            "Commerzbank",
            "Frankfurt, Germany",
            "Analyze investment opportunities for international clients. Work in English-speaking investment team. Requirements: Finance degree, analytical skills, market research experience, Excel proficiency.",
            "https://jobs.commerzbank.com",
            "Full-time",
            "Junior",
            "€48k - €62k",
            LocalDateTime.now().minusDays(5),
            "Commerzbank Careers"
        );
        job17.setCategory("Banking");
        job17.setSkills(new String[]{"Financial Analysis", "Investment", "Excel", "Market Research", "Finance"});

        JobPosting job18 = new JobPosting(
            "Compliance Officer - International Banking",
            "N26 Bank",
            "Berlin, Germany",
            "Ensure regulatory compliance for our digital bank. English-first company. Monitor transactions, KYC procedures, anti-money laundering. Requirements: Compliance experience, banking regulations knowledge, detail-oriented.",
            "https://n26.com/careers",
            "Full-time",
            "Mid-Senior",
            "€55k - €70k",
            LocalDateTime.now().minusDays(1),
            "N26 Careers"
        );
        job18.setCategory("Banking");
        job18.setSkills(new String[]{"Compliance", "KYC", "AML", "Banking Regulations", "Risk Management"});

        // School/Education Jobs
        JobPosting job19 = new JobPosting(
            "English Teacher - International School",
            "Berlin International School",
            "Berlin, Germany",
            "Teach English language and literature to international students (grades 6-12). IB curriculum experience preferred. Native English speaker. Requirements: Teaching certification, Bachelor's degree, classroom management.",
            "https://www.berlin-international-school.de/careers",
            "Full-time",
            "Mid",
            "€40k - €55k",
            LocalDateTime.now().minusDays(6),
            "Berlin International School"
        );
        job19.setCategory("Education");
        job19.setSkills(new String[]{"Teaching", "English", "IB Curriculum", "Classroom Management", "Education"});

        JobPosting job20 = new JobPosting(
            "School Administrator - International Education",
            "Munich International School",
            "Munich, Germany",
            "Coordinate administrative operations for our K-12 international school. Manage admissions, student records, parent communication. English-speaking environment. Requirements: School administration experience, organizational skills.",
            "https://www.mis-munich.de/careers",
            "Full-time",
            "Mid",
            "€38k - €50k",
            LocalDateTime.now().minusDays(4),
            "Munich International School"
        );
        job20.setCategory("Education");
        job20.setSkills(new String[]{"School Administration", "Student Records", "Admissions", "Parent Communication"});

        JobPosting job21 = new JobPosting(
            "Academic Counselor (English-Speaking)",
            "Frankfurt International School",
            "Frankfurt, Germany",
            "Guide students through academic planning and university preparation. English-first environment. Support students aged 14-18. Requirements: Counseling degree, university admissions knowledge, empathy.",
            "https://www.fis.edu/careers",
            "Full-time",
            "Mid-Senior",
            "€42k - €56k",
            LocalDateTime.now().minusDays(3),
            "Frankfurt International School"
        );
        job21.setCategory("Education");
        job21.setSkills(new String[]{"Academic Counseling", "University Admissions", "Student Support", "Guidance"});

        JobPosting job22 = new JobPosting(
            "Data Entry Administrator - Educational Records",
            "Goethe University",
            "Frankfurt, Germany",
            "Manage student records and educational data for international programs. Process applications, grades, transcripts. English required. Requirements: Data management, accuracy, confidentiality, university administration experience.",
            "https://www.uni-frankfurt.de/jobs",
            "Full-time",
            "Entry-Mid",
            "€35k - €45k",
            LocalDateTime.now().minusDays(2),
            "Goethe University"
        );
        job22.setCategory("Education");
        job22.setSkills(new String[]{"Data Entry", "Student Records", "University Administration", "Confidentiality"});

        jobs.add(job11);
        jobs.add(job12);
        jobs.add(job13);
        jobs.add(job14);
        jobs.add(job15);
        jobs.add(job16);
        jobs.add(job17);
        jobs.add(job18);
        jobs.add(job19);
        jobs.add(job20);
        jobs.add(job21);
        jobs.add(job22);

        // Load comments for sample jobs
        for (JobPosting job : jobs) {
            String savedComment = commentRepository.getComment(job.getUrl());
            job.setComments(savedComment != null ? savedComment : "");
        }

        return jobs;
    }
}

