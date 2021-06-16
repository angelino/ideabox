(ns ideabox.shared.url)

(defn home-url [] "/")

(defn home-user-url [user-id]
  (str "/users/" user-id "/ideas"))

(defn archive-url [user-id]
  (str "/users/" user-id "/archive"))

(defn login-url []
  "/auth/login")

(defn logout-url []
  "/auth/logout")

(defn sessions-url []
  "/auth/sessions")

(defn signup-url []
  "/auth/signup")

(defn registrations-url []
  "/auth/registrations")

(defn ideas-url [user-id]
  (str "/users/" user-id "/ideas"))

(defn tagged-ideas-url [user-id tag]
  (str (ideas-url user-id)
       (when tag (str "?tag=" tag))))

(defn new-idea-url [user-id]
  (str (ideas-url user-id) "/new"))

(defn idea-url [{:keys [user-id id] :as idea}]
  (str (ideas-url user-id) "/" id))

(defn edit-idea-url [idea]
  (str (idea-url idea)  "/edit"))

(defn like-idea-url [idea]
  (str (idea-url idea) "/like"))

(defn archive-idea-url [idea]
  (str (idea-url idea) "/archive"))

(defn tags-url [user-id]
  (str "/users/" user-id "/tags"))
